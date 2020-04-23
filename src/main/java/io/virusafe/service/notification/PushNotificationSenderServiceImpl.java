package io.virusafe.service.notification;

import io.virusafe.domain.dto.PushNotificationDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.PushNotificationException;
import io.virusafe.service.pushnotification.PushNotificationService;
import io.virusafe.service.query.QuestionnaireQueryService;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Set;

@Service
@Slf4j
public class PushNotificationSenderServiceImpl implements PushNotificationSenderService {

    private final UserDetailsService userDetailsService;

    private final PushNotificationService pushNotificationService;

    private final QuestionnaireQueryService questionnaireQueryService;

    /**
     * Construct PushNotificationSenderServiceImpl using beans
     *
     * @param userDetailsService
     * @param pushNotificationService
     * @param questionnaireQueryService
     */
    public PushNotificationSenderServiceImpl(
            final UserDetailsService userDetailsService,
            final PushNotificationService pushNotificationService,
            final QuestionnaireQueryService questionnaireQueryService) {
        this.userDetailsService = userDetailsService;
        this.pushNotificationService = pushNotificationService;
        this.questionnaireQueryService = questionnaireQueryService;
    }

    @Override
    public void sendCustomPushNotifications(final QuestionnaireQuery questionnaireQuery, final String title,
                                            final String message) {
        Set<String> userGuids = questionnaireQueryService.searchInQuestionnaire(questionnaireQuery);

        sendNotificationsForConcreteUsers(userGuids, title, message);
    }

    @Override
    public void sendNotificationsForConcreteUsers(final Set<String> userGuids, final String title, final String message) {
        if (userGuids.isEmpty()) {
            throw new PushNotificationException("PushNotification Cannot find users by the query in elasticsearch");
        }

        Set<String> pushTokens = userDetailsService.findPushTokensForUserGuids(userGuids);
        if (pushTokens.isEmpty()) {
            throw new PushNotificationException(MessageFormat
                    .format("PushNotification Cannot find push tokens for selected {0} users", pushTokens.size()));
        }
        pushNotificationService
                .sendNotificationToTokens(pushTokens, PushNotificationDTO.builder().title(title).body(message).build());
        log.info("PushNotification Send {} push notifications with title '{}' and message '{}'", pushTokens.size(),
                title, message);
    }

}
