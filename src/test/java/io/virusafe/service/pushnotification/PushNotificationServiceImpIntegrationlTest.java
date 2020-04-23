package io.virusafe.service.pushnotification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import io.virusafe.domain.dto.PushNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushNotificationServiceImpIntegrationlTest {

    private static final String PUSH_TOKEN = "cjN_ZUZMRYq4gyOC7Xmw1J:APA91bFISyd7Vyq79NnqGotpg0Cj4ZzGXg3ylB3v-ePzXSYZ4-HmnL4xxXg5ULtDw_-IHYwW4XcK2hS63DwPvrkZUbS2yPtNaARehcUnvooCbkaTXnfDdv4tnTDcnvc3Gu0q-5D8h1mR";
    private static final String PUSH_TOKEN_1 = "edvFZnE4Tnyl29eKod1PFE:APA91bFztEz3T20kQJasLq9OKRiBpr3GF_Y55E2pbUJxYqkIhPjFcfSr1w8_zOPDHv3wl0J9Be6-irbM0IWGQ2p7cbKD6QV0F0B4JR7Lu83e9ZV446axADfYHCM9JxBKHNw8Y4-Pc-RL";

    private static final String MESSAGE_TITLE = "messageTitle";
    private static final String MESSAGE_BODY = "messageBody";
    private static final String VIRUSAFE_DEV_FIREBASE_ADMINSDK_JSON = "firebase-adminsdk.json";


    private PushNotificationServiceImpl pushNotificationService;

    @BeforeEach
    public void setUp() throws IOException {
        pushNotificationService = new PushNotificationServiceImpl(createFirebaseMessaging());
    }

    @Test
    @Disabled
    void sendNotificationToTokens() {
        pushNotificationService
                .sendNotificationToTokens(Set.of(PUSH_TOKEN, PUSH_TOKEN_1),
                        createMockPushNotificationDTO());
    }

    private PushNotificationDTO createMockPushNotificationDTO() {
        PushNotificationDTO mockPushNotificationDTO = mock(PushNotificationDTO.class);
        when(mockPushNotificationDTO.getTitle()).thenReturn(MESSAGE_TITLE);
        when(mockPushNotificationDTO.getBody()).thenReturn(MESSAGE_BODY);
        return mockPushNotificationDTO;
    }

    private FirebaseMessaging createFirebaseMessaging() throws IOException {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(VIRUSAFE_DEV_FIREBASE_ADMINSDK_JSON);) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(in)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase init successfuly");
            }
        }

        return FirebaseMessaging.getInstance();
    }
}