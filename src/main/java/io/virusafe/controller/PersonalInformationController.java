package io.virusafe.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import io.virusafe.domain.dto.PersonalInformationResponseDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.mapper.PersonalInformationMapper;
import io.virusafe.security.advice.PersonalInfoRateLimit;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/personalinfo")
@ApiResponses(value = {
        @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 404, message = SwaggerConstants.USER_NOT_FOUND_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 500, message = SwaggerConstants.UNEXPECTED_ERROR_MESSAGE, response = ErrorDTO.class)
})
@Slf4j
public class PersonalInformationController {

    private static final String MISSING_USER_TEMPLATE = "User with phone number {0} is not registered!";

    private final UserDetailsService userDetailsService;
    private final PersonalInformationMapper personalInformationMapper;

    /**
     * Construct a new PersonalInformationController, using the autowired UserDetailsService and
     * PersonalInformationMapper beans.
     *
     * @param userDetailsService        the UserDetailsService to use
     * @param personalInformationMapper the PersonalInformationMapper to use
     */
    @Autowired
    public PersonalInformationController(final UserDetailsService userDetailsService,
                                         final PersonalInformationMapper personalInformationMapper) {
        this.userDetailsService = userDetailsService;
        this.personalInformationMapper = personalInformationMapper;
    }

    /**
     * POST /personalinfo endpoint. Used for submitting user personal information on behalf of the logged in user.
     * Rate limit defined by {@link PersonalInfoRateLimit} annotation.
     *
     * @param userPrincipal          SecurityContext UserPrincipal representing the logged in user
     * @param personalInformationDTO The personal information to persist
     */
    @PostMapping
    @PersonalInfoRateLimit
    @ApiOperation(value = "Submit user personal information")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header",
                    example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header",
                    example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = SwaggerConstants.SUBMITTED_SUCCESSFULLY_MESSAGE),
            @ApiResponse(code = 412, message = SwaggerConstants.FAILED_VALIDATION_MESSAGE,
                    response = ValidationErrorDTO.class),
            @ApiResponse(code = 429, message = SwaggerConstants.RATE_LIMIT_EXCEEDED_MESSAGE, response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void addPersonalInformation(
            final @ApiIgnore @AuthenticationPrincipal UserPrincipal userPrincipal,
            final @Valid @RequestBody PersonalInformationRequestDTO
                    personalInformationDTO) {

        String userGuid = userPrincipal.getUserGuid();
        log.info("POST request for /personalinfo. UserID: {}", userGuid);
        PersonalInformationUpdateCommand personalInformationUpdateCommand = personalInformationMapper
                .mapToUpdateCommand(personalInformationDTO);

        userDetailsService.updatePersonalInformation(userGuid, personalInformationUpdateCommand);
    }

    /**
     * GET /personalinfo endpoint. Used for fetching user personal information.
     * Returns the currently logged in user's personal information.
     *
     * @param userPrincipal SecurityContext UserPrincipal representing the logged in user
     */
    @GetMapping
    @ApiOperation("Get the current user's personal information")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header",
                    example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header",
                    example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    public ResponseEntity<PersonalInformationResponseDTO> getPersonalInformation(
            final @ApiIgnore @AuthenticationPrincipal
                    UserPrincipal userPrincipal) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("GET request for /personalinfo. UserID: {}", userGuid);

        UserDetails dbUser = userDetailsService.findByUserGuid(userGuid)
                .orElseThrow(
                        () -> new NoSuchElementException(MessageFormat.format(MISSING_USER_TEMPLATE,
                                userPrincipal.getPhoneNumber()))
                );
        PersonalInformationResponseDTO personalInformationResponseDTO = personalInformationMapper
                .mapToResponseDTO(dbUser);

        return ResponseEntity.ok(personalInformationResponseDTO);
    }

    /**
     * DELETE /personalinfo endpoint. Used for removing user personal information.
     * Sets the currently logged in user's personal information to null.
     * Rate limit defined by {@link PersonalInfoRateLimit} annotation.
     *
     * @param userPrincipal SecurityContext UserPrincipal representing the logged in user
     */
    @DeleteMapping
    @PersonalInfoRateLimit
    @ApiOperation("Delete the current user's personal information")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header",
                    example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header",
                    example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 429, message = SwaggerConstants.RATE_LIMIT_EXCEEDED_MESSAGE, response = ErrorDTO.class)
    })
    public void deletePersonalInformation(final @ApiIgnore @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("DELETE request for /personalinfo. UserGuid: {}", userGuid);

        userDetailsService.deleteByUserGuid(userGuid);
    }
}
