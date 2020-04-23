package io.virusafe.service.pushnotification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.TopicManagementResponse;
import io.virusafe.domain.dto.PushNotificationDTO;
import io.virusafe.exception.PushNotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceImplTest {

    private static final String PUSH_TOKEN = "pushToken";
    private static final String MESSAGE_TITLE = "messageTitle";
    private static final String MESSAGE_BODY = "messageBody";
    private static final String TEST_ERROR_CODE = "testErrorCode";
    private static final String TEST_TOPIC = "testTopic";
    private static final String TEST_REASON = "testReason";

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private PushNotificationServiceImpl pushNotificationService;

    @BeforeEach
    public void setUp() {
        pushNotificationService = new PushNotificationServiceImpl(firebaseMessaging);
    }

    @Test
    void sendNotificationToTokens() throws FirebaseMessagingException {
        BatchResponse mockedBatchResponse = mock(BatchResponse.class);
        when(mockedBatchResponse.getFailureCount()).thenReturn(0);
        when(firebaseMessaging.sendMulticast(any())).thenReturn(mockedBatchResponse);
        pushNotificationService
                .sendNotificationToTokens(Set.of(PUSH_TOKEN),
                        createMockPushNotificationDTO());
        ArgumentCaptor<MulticastMessage> captotMulticastMessage = ArgumentCaptor.forClass(MulticastMessage.class);
        verify(firebaseMessaging, times(1)).sendMulticast(captotMulticastMessage.capture());
        assertNotNull(captotMulticastMessage.getValue());
    }

    @Test
    void sendNotificationToTokensProviderReturnBadRequest() throws FirebaseMessagingException {
        BatchResponse mockedBatchResponse = mock(BatchResponse.class);
        when(mockedBatchResponse.getFailureCount()).thenReturn(1);
        SendResponse mockedSendResponse = mock(SendResponse.class);
        FirebaseMessagingException mockedException = mock(FirebaseMessagingException.class);
        when(mockedException.getErrorCode()).thenReturn(TEST_ERROR_CODE);
        when(mockedSendResponse.getException()).thenReturn(mockedException);
        when(mockedBatchResponse.getResponses()).thenReturn(Collections.singletonList(mockedSendResponse));

        when(firebaseMessaging.sendMulticast(any())).thenReturn(mockedBatchResponse);
        PushNotificationException exceptionThrown = assertThrows(PushNotificationException.class,
                () -> pushNotificationService
                        .sendNotificationToTokens(Set.of(PUSH_TOKEN),
                                createMockPushNotificationDTO()));
        assertEquals("Could not send push notification to 1 tokens from 1 : error codes [testErrorCode]",
                exceptionThrown.getMessage());
    }

    @Test
    void sendNotificationToTokensProviderThrowException() throws FirebaseMessagingException {
        when(firebaseMessaging.sendMulticast(any())).thenThrow(FirebaseMessagingException.class);
        assertThrows(PushNotificationException.class,
                () -> pushNotificationService
                        .sendNotificationToTokens(Set.of(PUSH_TOKEN),
                                createMockPushNotificationDTO()));
    }

    private PushNotificationDTO createMockPushNotificationDTO() {
        PushNotificationDTO mockPushNotificationDTO = mock(PushNotificationDTO.class);
        when(mockPushNotificationDTO.getTitle()).thenReturn(MESSAGE_TITLE);
        when(mockPushNotificationDTO.getBody()).thenReturn(MESSAGE_BODY);
        return mockPushNotificationDTO;
    }

    @Test
    void sendNotificationToTokensNullTokenList() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.sendNotificationToTokens(null, mock(PushNotificationDTO.class)));
    }

    @Test
    void sendNotificationToTokensNullPushNotificationDTO() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.sendNotificationToTokens(Collections.emptySet(), null));
    }

    @Test
    void sendNotificationToTopic() throws FirebaseMessagingException {
        pushNotificationService
                .sendNotificationToTopic(TEST_TOPIC,
                        createMockPushNotificationDTO());
        verify(firebaseMessaging, times(1)).send(any());
    }

    @Test
    void sendNotificationToTopicProviderThrowException() throws FirebaseMessagingException {
        when(firebaseMessaging.send(any())).thenThrow(FirebaseMessagingException.class);
        assertThrows(PushNotificationException.class,
                () -> pushNotificationService
                        .sendNotificationToTopic(TEST_TOPIC,
                                createMockPushNotificationDTO()));
    }

    @Test
    void sendNotificationToTopicNullTokenList() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.sendNotificationToTopic(null, mock(PushNotificationDTO.class)));
    }

    @Test
    void sendNotificationToTopicNullPushNotificationDTO() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.sendNotificationToTopic(TEST_TOPIC, null));
    }

    @Test
    void subscribeUserToTopic() throws FirebaseMessagingException {
        when(firebaseMessaging.subscribeToTopic(any(), any())).thenReturn(mock(TopicManagementResponse.class));
        pushNotificationService
                .subscribeUserToTopic(TEST_TOPIC, Set.of(PUSH_TOKEN));
        verify(firebaseMessaging, times(1)).subscribeToTopic(any(), any());
    }

    @Test
    void subscribeUserToTopicErrorResponse() throws FirebaseMessagingException {
        TopicManagementResponse mockTopicManagementResponse = mock(TopicManagementResponse.class);
        when(mockTopicManagementResponse.getFailureCount()).thenReturn(1);
        TopicManagementResponse.Error mockedError = mock(TopicManagementResponse.Error.class);
        when(mockedError.getReason()).thenReturn(TEST_REASON);
        when(mockTopicManagementResponse.getErrors()).thenReturn(Collections.singletonList(mockedError));
        when(firebaseMessaging.subscribeToTopic(any(), any())).thenReturn(mockTopicManagementResponse);
        PushNotificationException pushNotificationException = assertThrows(PushNotificationException.class,
                () -> pushNotificationService.subscribeUserToTopic(TEST_TOPIC, Set.of(PUSH_TOKEN)));
        assertEquals("Could not subscribe some tokens: [testReason]", pushNotificationException.getMessage());
    }

    @Test
    void subscribeUserToTopicNullTokenList() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.subscribeUserToTopic(null, Collections.emptySet()));
    }

    @Test
    void subscribeUserToTopicNullPushNotificationDTO() {
        assertThrows(NullPointerException.class,
                () -> pushNotificationService.sendNotificationToTopic(TEST_TOPIC, null));
    }

    @Test
    void subscribeUserToTopicProviderThrowException() throws FirebaseMessagingException {
        when(firebaseMessaging.subscribeToTopic(any(), any())).thenThrow(FirebaseMessagingException.class);
        assertThrows(PushNotificationException.class,
                () -> pushNotificationService
                        .subscribeUserToTopic(TEST_TOPIC, Set.of(PUSH_TOKEN)));
    }
}