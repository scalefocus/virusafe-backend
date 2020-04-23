package io.virusafe.service.pushnotification;

import io.virusafe.domain.dto.PushNotificationDTO;

import java.util.Set;

/**
 * Push Notification Service
 */
public interface PushNotificationService {
    /**
     * Send notification to tokens
     *
     * @param pushToken
     * @param pushNotificationDTO
     */
    void sendNotificationToTokens(final Set<String> pushToken, final PushNotificationDTO pushNotificationDTO);

    /**
     * Send notification to topic
     * NOTE: Currently not supported by mobile apps
     *
     * @param topic
     * @param pushNotificationDTO
     */
    void sendNotificationToTopic(String topic, PushNotificationDTO pushNotificationDTO);

    /**
     * Subscribe user to topic
     *
     * @param pushTokens
     * @param topic
     */
    void subscribeUserToTopic(String topic, Set<String> pushTokens);
}
