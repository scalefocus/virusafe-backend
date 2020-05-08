package io.virusafe.service.notification;

import io.virusafe.domain.query.QuestionnaireQuery;

import java.util.Set;

public interface PushNotificationSenderService {

    /**
     * sends custom push notifications
     *
     * @return
     */
    void sendCustomPushNotifications(final QuestionnaireQuery questionnaireQuery, final String title,
                                     final String message, final boolean reverseQueryResults);

    /**
     * sends custom push notifications for concrete users
     *
     * @return
     */
    void sendNotificationsForConcreteUsers(final Set<String> userGuids, final String title, final String message);
}
