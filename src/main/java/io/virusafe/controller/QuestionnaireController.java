package io.virusafe.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.dto.QuestionDTO;
import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.exception.InvalidPersonalInformationException;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.security.advice.QuestionnaireTimeout;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.questionnaire.QuestionnaireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/questionnaire")
@ApiResponses(value = {
        @ApiResponse(code = 401, message = SwaggerConstants.UNAUTHORIZED_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 403, message = SwaggerConstants.ACCESS_FORBIDDEN_MESSAGE, response = ErrorDTO.class),
        @ApiResponse(code = 500, message = SwaggerConstants.UNEXPECTED_ERROR_MESSAGE, response = ErrorDTO.class)
})
@Slf4j
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    /**
     * Construct a new QuestionnaireController, using the autowired QuestionnaireService bean.
     *
     * @param questionnaireService the QuestionnaireService to use
     */
    @Autowired
    public QuestionnaireController(final QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    /**
     * GET /questionnaire endpoint. Used for fetching all questionnaire questions in a given language.
     *
     * @param language request header denoting the language to use
     * @return the complete list of questions in a given language
     */
    @GetMapping
    @ApiOperation(value = "Get all questionnaire questions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header", example = SwaggerConstants.BEARER_TOKEN, required = true),
            @ApiImplicitParam(name = "language", value = "Language", paramType = "header", example = SwaggerConstants.LANGUAGE)
    })
    public ResponseEntity<List<QuestionDTO>> getQuestionnaire(
            @RequestHeader(required = false) final String language) {
        log.info("GET request for /questionnaire. Language {} ", language);
        List<QuestionDTO> questionnaires = questionnaireService.getQuestionnaire(language);
        return new ResponseEntity<>(questionnaires, HttpStatus.OK);
    }

    /**
     * POST /questionnaire endpoint. Used for submitting questionnaire answers on behalf of the logged in user.
     * Rate limit defined by {@link QuestionnaireTimeout} annotation.
     *
     * @param userPrincipal        SecurityContext UserPrincipal representing the logged in user
     * @param questionnairePostDTO the questionnaire answers to submit
     */
    @PostMapping
    @QuestionnaireTimeout
    @ApiOperation(value = "Submit a user's questionnaire actions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clientId", value = "Client ID", paramType = "header", example = SwaggerConstants.CLIENT_ID, required = true),
            @ApiImplicitParam(name = "Authorization", value = "JWT token", paramType = "header", example = SwaggerConstants.BEARER_TOKEN, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully submitted questionnaire"),
            @ApiResponse(code = 404, message = SwaggerConstants.USER_NOT_FOUND_MESSAGE, response = ErrorDTO.class),
            @ApiResponse(code = 412, message = SwaggerConstants.FAILED_VALIDATION_MESSAGE,
                    response = ValidationErrorDTO.class),
            @ApiResponse(code = 429, message = SwaggerConstants.RATE_LIMIT_EXCEEDED_MESSAGE, response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public void postQuestionnaire(@ApiIgnore @AuthenticationPrincipal final UserPrincipal userPrincipal,
                                  @Valid @RequestBody final QuestionnairePostDTO questionnairePostDTO) {
        String userGuid = userPrincipal.getUserGuid();
        log.info("POST request for /questionnaire. UserID: {}", userGuid);

        // If not identification number is present for the user, throw an exception as we can't submit their questionnaire.
        if (Objects.isNull(userPrincipal.getIdentificationNumber())) {
            throw new InvalidPersonalInformationException(userGuid);
        }
        questionnaireService.postQuestionnaire(questionnairePostDTO, userGuid);
    }
}
