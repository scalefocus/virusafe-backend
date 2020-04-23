package io.virusafe.service.pushnotification;

import ch.qos.logback.classic.Level;
import io.virusafe.domain.dto.PushNotificationDTO;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogOnlyPushNotificationServiceTest {

    private static final String TEST_TOKEN = "testToken";
    private static final String MESSAGE_TITLE = "messageTitle";
    private static final String MESSAGE_BODY = "messageBody";
    private static final String TEST_TOPIC = "testTopic";
    private static final String EXPECTED_SEND_NOTIFICATION_TO_TOKEN = "LogOnly sendNotificationToTokens push notifications pushTokens [testToken] and messageData PushNotificationDTO(title=messageTitle, body=messageBody, imageUrl=null)";
    private static final String EXPECTED_SEND_NOTIFICATION_TO_TOPIC = "LogOnly sendNotificationToTopic push notifications topic testTopic and messageData PushNotificationDTO(title=messageTitle, body=messageBody, imageUrl=null)";
    private static final String EXPECTED_SUBSCRIBE_USER_TO_TOPIC = "LogOnly subscribeUserToTopic push notifications topic testTopic and pushTokens [testToken]";

    @Test
    void sendNotificationToTokens() {
        PushNotificationService pushNotificationService = new LogOnlyPushNotificationService();
        LogCaptor<LogOnlyPushNotificationService> logCaptor = LogCaptor.forClass(LogOnlyPushNotificationService.class);

        pushNotificationService.sendNotificationToTokens(Set.of(TEST_TOKEN), createMockPushNotificationDTO());
        assertEquals(EXPECTED_SEND_NOTIFICATION_TO_TOKEN, logCaptor.getLogs(Level.INFO).get(0));
    }

    private PushNotificationDTO createMockPushNotificationDTO() {
        return PushNotificationDTO.builder().title(MESSAGE_TITLE).body(MESSAGE_BODY).build();
    }

    @Test
    void sendNotificationToTopic() {
        PushNotificationService pushNotificationService = new LogOnlyPushNotificationService();
        LogCaptor<LogOnlyPushNotificationService> logCaptor = LogCaptor.forClass(LogOnlyPushNotificationService.class);

        pushNotificationService.sendNotificationToTopic(TEST_TOPIC, createMockPushNotificationDTO());
        assertEquals(EXPECTED_SEND_NOTIFICATION_TO_TOPIC, logCaptor.getLogs(Level.INFO).get(0));
    }

    @Test
    void subscribeUserToTopic() {
        PushNotificationService pushNotificationService = new LogOnlyPushNotificationService();
        LogCaptor<LogOnlyPushNotificationService> logCaptor = LogCaptor.forClass(LogOnlyPushNotificationService.class);

        pushNotificationService.subscribeUserToTopic(TEST_TOPIC, Set.of(TEST_TOKEN));
        assertEquals(EXPECTED_SUBSCRIBE_USER_TO_TOPIC, logCaptor.getLogs(Level.INFO).get(0));
    }
}