package io.virusafe.service.pushnotification;

import io.virusafe.domain.dto.PushNotificationDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class LogOnlyPushNotificationService implements PushNotificationService {
    @Override
    public void sendNotificationToTokens(final Set<String> pushTokens, final PushNotificationDTO pushNotificationDTO) {
        log.info("LogOnly sendNotificationToTokens push notifications pushTokens {} and messageData {}", pushTokens,
                pushNotificationDTO);
    }

    @Override
    public void sendNotificationToTopic(final String topic, final PushNotificationDTO pushNotificationDTO) {
        log.info("LogOnly sendNotificationToTopic push notifications topic {} and messageData {}", topic,
                pushNotificationDTO);
    }

    @Override
    public void subscribeUserToTopic(final String topic, final Set<String> pushTokens) {
        log.info("LogOnly subscribeUserToTopic push notifications topic {} and pushTokens {}", topic, pushTokens);
    }
}
