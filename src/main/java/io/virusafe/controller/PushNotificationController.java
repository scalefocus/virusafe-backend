package io.virusafe.controller;

import io.swagger.annotations.ApiOperation;
import io.virusafe.domain.dto.CustomPushNotificationDTO;
import io.virusafe.domain.dto.PushNotificationRequestDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.mapper.QuestionnaireQueryMapper;
import io.virusafe.service.notification.PushNotificationSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/pushNotification")
@Slf4j
public class PushNotificationController {

    private final PushNotificationSenderService pushNotificationService;

    private final QuestionnaireQueryMapper questionnaireQueryMapper;

    /**
     * Construct a new PushNotificationController, using the autowired PushNotificationSenderService, QuestionnaireQueryMapper.
     *
     * @param pushNotificationService  the PushNotificationSenderService to use
     * @param questionnaireQueryMapper the QuestionnaireQueryMapper to use
     */
    public PushNotificationController(
            final PushNotificationSenderService pushNotificationService,
            final QuestionnaireQueryMapper questionnaireQueryMapper) {
        this.pushNotificationService = pushNotificationService;
        this.questionnaireQueryMapper = questionnaireQueryMapper;
    }

    /**
     * POST /query endpoint. Submits a PushNotificationRequestDTO which contains a set of query specifications.
     *
     * @param pushNotificationRequestDTO describes the query specifications
     */
    @PostMapping(value = "/query")
    @ApiOperation(value = "Send a new push notification depending on a set of custom query params")
    @ResponseStatus(HttpStatus.OK)
    public void sendPushNotification(final @Valid @RequestBody PushNotificationRequestDTO pushNotificationRequestDTO) {

        QuestionnaireQuery questionnaireQuery = questionnaireQueryMapper
                .mapQuestionnaireQueryDTOToQuestionnaireQuery(pushNotificationRequestDTO.getQuestionnaireQuery());

        pushNotificationService
                .sendCustomPushNotifications(questionnaireQuery, pushNotificationRequestDTO.getMessage(),
                        pushNotificationRequestDTO.getTitle(), pushNotificationRequestDTO.isReverseQueryResults());
    }

    /**
     * POST /user endpoint. Submits a CustomPushNotificationDTO which contains a set of userGuids.
     *
     * @param customPushNotificationDTO describes the receivers, the message and the title of the notification
     */
    @PostMapping(value = "/user")
    @ApiOperation(value = "Send a new push notification to specific users")
    @ResponseStatus(HttpStatus.OK)
    public void sendPushNotificationForConcreteUsers(
            final @Valid @RequestBody CustomPushNotificationDTO customPushNotificationDTO) {

        pushNotificationService.sendNotificationsForConcreteUsers(customPushNotificationDTO.getUserGuids(),
                customPushNotificationDTO.getTitle(), customPushNotificationDTO.getMessage());
    }

}
