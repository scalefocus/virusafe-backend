package io.virusafe.service.pushnotification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.TopicManagementResponse;
import io.virusafe.domain.dto.PushNotificationDTO;
import io.virusafe.exception.PushNotificationException;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

    private static final String COULD_NOT_SEND_PUSH_NOTIFICATION_CODE_MESSAGE = "Could not send push notification: code {0}, message {1}";
    private static final String COULD_NOT_SEND_PUSH_NOTIFICATION_BATCH_MESSAGE = "Could not send push notification to {0} tokens from {1} : error codes {2}";
    private static final String COULD_NOT_SUBSCRIBE_TO_TOPIC_CODE_MESSAGE = "Could not subscribe to topic: code {0}, message {1}";
    private static final String COULD_NOT_SUBSCRIBE_SOME_TOKENS = "Could not subscribe some tokens: {0}";

    private final FirebaseMessaging firebaseMessaging;

    /**
     * Construct PushNotificationServiceImpl
     *
     * @param firebaseMessaging
     */
    public PushNotificationServiceImpl(final FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void sendNotificationToTokens(final Set<String> pushToken, final PushNotificationDTO pushNotificationDTO) {
        Objects.requireNonNull(pushToken);
        Objects.requireNonNull(pushNotificationDTO);

        log.debug("Sending push notification to tokens {}", pushToken);
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(pushToken)
                .setNotification(createNotification(pushNotificationDTO))
                .putData(pushNotificationDTO.getTitle(), pushNotificationDTO.getBody())
                .build();
        sendMulticastMessageToFirebase(message);
    }

    private void sendMulticastMessageToFirebase(final MulticastMessage message) {
        try {
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            if (response.getFailureCount() > 0) {
                List<String> errorCodes = response.getResponses().stream()
                        .filter(Predicate.not(SendResponse::isSuccessful))
                        .map(SendResponse::getException)
                        .map(FirebaseMessagingException::getErrorCode).distinct()
                        .collect(Collectors.toList());

                String errorMessage = MessageFormat
                        .format(COULD_NOT_SEND_PUSH_NOTIFICATION_BATCH_MESSAGE, response.getFailureCount(),
                                response.getResponses().size(), errorCodes);
                log.error(errorMessage);
                throw new PushNotificationException(errorMessage);
            }
            log.debug("Multicast push notification Firebase response: {}", response);
        } catch (FirebaseMessagingException e) {
            throw createPushNotificationException(e, COULD_NOT_SEND_PUSH_NOTIFICATION_CODE_MESSAGE);
        }
    }

    private PushNotificationException createPushNotificationException(final FirebaseMessagingException e,
                                                                      final String formatTemplate) {
        String errorMessage = MessageFormat
                .format(formatTemplate, e.getErrorCode(), e.getMessage());
        log.error(errorMessage, e);
        return new PushNotificationException(errorMessage, e);
    }

    @Override
    public void sendNotificationToTopic(final String topic, final PushNotificationDTO pushNotificationDTO) {
        Objects.requireNonNull(topic);
        Objects.requireNonNull(pushNotificationDTO);

        log.debug("Sending push notification to topic {}", topic);
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(createNotification(pushNotificationDTO))
                .putData(pushNotificationDTO.getTitle(), pushNotificationDTO.getBody())
                .build();
        try {
            String response = firebaseMessaging.send(message);
            log.debug("Push notification Firebase response: {}", response);
        } catch (FirebaseMessagingException e) {
            throw createPushNotificationException(e, COULD_NOT_SEND_PUSH_NOTIFICATION_CODE_MESSAGE);
        }
    }

    private Notification createNotification(final PushNotificationDTO pushNotificationDTO) {
        return Notification.builder()
                .setTitle(pushNotificationDTO.getTitle())
                .setImage(pushNotificationDTO.getImageUrl())
                .setBody(pushNotificationDTO.getBody())
                .build();
    }

    @Override
    public void subscribeUserToTopic(final String topic, final Set<String> pushTokens) {
        Objects.requireNonNull(topic);
        Objects.requireNonNull(pushTokens);

        log.debug("Subscribing tokens {} to topic {}", pushTokens, topic);
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(new ArrayList<>(pushTokens), topic);
            log.debug("Subscribe attempt Firebase response: {}", response);
            if (response.getFailureCount() > 0) {
                List<String> errorReasons = response.getErrors().stream()
                        .map(TopicManagementResponse.Error::getReason)
                        .collect(Collectors.toList());

                String errorMessage = MessageFormat
                        .format(COULD_NOT_SUBSCRIBE_SOME_TOKENS, errorReasons);
                log.error(errorMessage);
                throw new PushNotificationException(errorMessage);
            }
        } catch (FirebaseMessagingException e) {
            throw createPushNotificationException(e, COULD_NOT_SUBSCRIBE_TO_TOPIC_CODE_MESSAGE);
        }
    }
}
