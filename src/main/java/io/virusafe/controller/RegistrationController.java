package io.virusafe.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.dto.AccessTokenDTO;
import io.virusafe.domain.dto.PinGenerationDTO;
import io.virusafe.domain.dto.PushTokenDTO;
import io.virusafe.domain.dto.TokenGenerationDTO;
import io.virusafe.domain.dto.TokenRefreshDTO;
import io.virusafe.exception.UnverifiablePinException;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.security.advice.PinRateLimit;
import io.virusafe.security.advice.PushTokenRateLimit;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.pin.PinService;
import io.virusafe.service.token.TokenService;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@ApiResponses(value = {
        @ApiResponse(code = 412, message = SwaggerConstants.FAILED_VALIDATION_MESSAGE,
                response = ValidationErrorDTO.class),
        @ApiResponse(code = 500, message = SwaggerConstants.UNEXPECTED_ERROR_MESSAGE, response = ErrorDTO.class)
})
@Slf4j
public class RegistrationController {

    private final TokenService tokenService;

    private final PinService pinService;

    private final UserDetailsService userDetailsService;

    /**
     * Construct a new RegistrationController, using the autowired TokenService, PinService and UserDetailsService beans.
     *
     * @param tokenService       the TokenService to use
     * @param pinService         the PinService to use
     * @param userDetailsService the UserDetailsService to use
     */
    @Autowired
    public RegistrationController(final TokenService tokenService,
                                  final PinService pinService,
                                  final UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.pinService = pinService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * POST /pin endpoint. Submits a PIN generation request for a passed phone number.
     * Rate limit defined by the {@link PinRateLimit} annotation.
     *
     * @param pinGenerationDTO the phone number to generate a PIN for
     */
    @PostMapping(value = "/pin")
    @PinRateLimit
    @ApiOperation(value = "Generate a verification PIN and send it to the given phone number")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "PIN created and request sent to the SMS service"),
            @ApiResponse(code = 429, message = SwaggerConstants.RATE_LIMIT_EXCEEDED_MESSAGE, response = ErrorDTO.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void createPin(final @Valid @RequestBody PinGenerationDTO pinGenerationDTO) {
        log.info("POST request for /pin. Phone number: {}", pinGenerationDTO.getPhoneNumber());

        pinService.generatePin(pinGenerationDTO.getPhoneNumber());
    }

    /**
     * POST /token endpoint. Generates an access token and refresh token for a given phone number and PIN,
     * provided that they match.
     *
     * @param tokenGenerationDTO the phone number and PIN to generate a token for
     * @return the generated access token and refresh token
     */
    @PostMapping(value = "/token")
    @ApiOperation(value = "Verify that the passed PIN and phone number match and generate an access token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "PIN matches, JWT generated"),
            @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 438, message = "Could not verify matching PIN and phone number",
                    response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccessTokenDTO> createToken(
            final @Valid @RequestBody TokenGenerationDTO tokenGenerationDTO) {
        log.info("POST request for /token. Phone number: {}", tokenGenerationDTO.getPhoneNumber());

        if (pinService.verifyPin(tokenGenerationDTO.getPhoneNumber(), tokenGenerationDTO.getPin())) {
            AccessTokenDTO tokenResponseDTO = tokenService.generateToken(tokenGenerationDTO.getPhoneNumber());
            // Invalidate all current PINs after we've generated a token successfully.
            pinService.invalidatePins(tokenGenerationDTO.getPhoneNumber());
            return new ResponseEntity<>(tokenResponseDTO, HttpStatus.CREATED);
        } else {
            throw new UnverifiablePinException(tokenGenerationDTO.getPhoneNumber(), tokenGenerationDTO.getPin());
        }
    }

    /**
     * POST /pushtoken endpoint. Registers a new push token on behalf of the logged in user.
     * Rate limit defined by the {@link PushTokenRateLimit} annotation.
     *
     * @param userPrincipal SecurityContext UserPrincipal representing the logged in user
     * @param pushTokenDTO  the push token to register
     */
    @PostMapping(value = "/pushtoken")
    @PushTokenRateLimit
    @ApiOperation(value = "Register a new notification push token for the user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header", example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered push token"),
            @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 404, message = SwaggerConstants.USER_NOT_FOUND_MESSAGE, response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.OK)
    public void addPushToken(final @ApiIgnore @AuthenticationPrincipal UserPrincipal userPrincipal,
                             final @Valid @RequestBody PushTokenDTO pushTokenDTO) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("POST request for /pushtoken. UserID: {}", userGuid);
        userDetailsService.updatePushToken(userGuid, pushTokenDTO.getPushToken());
    }

    /**
     * POST /token/refresh endpoint. Generates a new access token from the given refresh token.
     *
     * @param tokenRefreshDTO the refresh token to generate a new access token for
     * @return the generated access token and refresh token
     */
    @PostMapping(value = "/token/refresh")
    @ApiOperation(value = "Generate a new access token from a refresh token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Access token matches, JWT generated"),
            @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 438, message = "Could not verify refresh token validity", response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccessTokenDTO> refreshToken(final @Valid @RequestBody TokenRefreshDTO tokenRefreshDTO) {
        log.info("POST request for /token.");
        return new ResponseEntity<>(tokenService.refreshToken(tokenRefreshDTO.getRefreshToken()), HttpStatus.CREATED);
    }
}
