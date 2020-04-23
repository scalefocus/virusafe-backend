package io.virusafe.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.LocationGpsDTO;
import io.virusafe.domain.dto.LocationProximityDTO;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.mapper.LocationGpsMapper;
import io.virusafe.security.advice.LocationTimeout;
import io.virusafe.security.advice.ProximityRateLimit;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.location.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/location")
@Slf4j
@ApiResponses(value = {
        @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 404, message = SwaggerConstants.USER_NOT_FOUND_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 412, message = SwaggerConstants.FAILED_VALIDATION_MESSAGE,
                response = ValidationErrorDTO.class),
        @ApiResponse(code = 429, message = SwaggerConstants.RATE_LIMIT_EXCEEDED_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 500, message = SwaggerConstants.UNEXPECTED_ERROR_MESSAGE, response = ErrorDTO.class)
})
public class LocationController {

    private final LocationService locationService;
    private final LocationGpsMapper locationGpsMapper;

    /**
     * Construct a new LocationController, using the autowired LocationService and LocationGpsMapper beans.
     *
     * @param locationService   the LocationService to use
     * @param locationGpsMapper the LocationGpsMapper to use
     */
    @Autowired
    public LocationController(final LocationService locationService,
                              final LocationGpsMapper locationGpsMapper) {
        this.locationService = locationService;
        this.locationGpsMapper = locationGpsMapper;
    }

    /**
     * POST /location/gps endpoint. Used for submitting GPS coordinates on behalf of the logged in user.
     * Rate limit defined by {@link LocationTimeout} annotation.
     *
     * @param userPrincipal  SecurityContext UserPrincipal representing the logged in user
     * @param locationGpsDTO The location details to persist
     */
    @PostMapping(value = "/gps")
    @LocationTimeout
    @ApiOperation(value = "Submit GPS coordinates")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header", example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = SwaggerConstants.SUBMITTED_SUCCESSFULLY_MESSAGE)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void gpsLocation(@ApiIgnore @AuthenticationPrincipal final UserPrincipal userPrincipal,
                            @RequestBody final LocationGpsDTO locationGpsDTO) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("POST request for /gps. UserGuid: {}", userGuid);

        LocationDocument locationDocument = locationGpsMapper.mapToLocationDocument(userGuid, locationGpsDTO);
        locationService.createLocation(locationDocument);
    }

    /**
     * POST /location/proximity endpoint. Used for submitting details on all devices within proximity
     * on behalf of the logged in user.
     * Rate limit defined by {@link ProximityRateLimit} annotation.
     *
     * @param userPrincipal        SecurityContext UserPrincipal representing the logged in user
     * @param locationProximityDTO The location proximity details to persist
     */
    @PostMapping(value = "/proximity")
    @ProximityRateLimit
    @ApiOperation(value = "Submit devices in proximity")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header", example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = SwaggerConstants.SUBMITTED_SUCCESSFULLY_MESSAGE)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void proximityLocation(@ApiIgnore @AuthenticationPrincipal final UserPrincipal userPrincipal,
                                  @Valid @RequestBody final LocationProximityDTO locationProximityDTO) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("POST request for /proximity.  UserGuid: {}", userGuid);

        locationService.postProximity(userGuid, locationProximityDTO);
    }
}
