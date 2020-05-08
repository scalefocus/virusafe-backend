package io.virusafe.service.notification;

import io.virusafe.domain.dto.PushNotificationDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.PushNotificationException;
import io.virusafe.service.pushnotification.PushNotificationService;
import io.virusafe.service.query.QuestionnaireQueryService;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                                            final String message, final boolean reverseQueryResults) {
        Set<String> userGuids = questionnaireQueryService.searchInQuestionnaire(questionnaireQuery);

        if (userGuids.isEmpty() && !reverseQueryResults) {
            throw new PushNotificationException("PushNotification Cannot find users by the query in elasticsearch");
        }

        Set<String> pushTokens = userDetailsService.findPushTokensForUserGuids(userGuids, reverseQueryResults);

        sendNotifications(title, message, pushTokens);
    }

    @Override
    public void sendNotificationsForConcreteUsers(final Set<String> userGuids, final String title,
                                                  final String message) {
        if (userGuids.isEmpty()) {
            throw new PushNotificationException("PushNotification Cannot find users by the query in elasticsearch");
        }

        Set<String> pushTokens = userDetailsService.findPushTokensForUserGuids(userGuids, false);

        sendNotifications(title, message, pushTokens);
    }

    private void sendNotifications(final String title, final String message, final Set<String> pushTokens) {
        if (pushTokens.isEmpty()) {
            throw new PushNotificationException("PushNotification Cannot find push tokens or user revoked its data");
        }

        pushNotificationService
                .sendNotificationToTokens(pushTokens, PushNotificationDTO.builder().title(title).body(message).build());
        log.info("PushNotification Send {} push notifications with title '{}' and message '{}'", pushTokens.size(),
                title, message);
    }

}
