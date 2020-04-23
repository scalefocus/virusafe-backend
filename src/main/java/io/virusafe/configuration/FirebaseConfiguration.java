package io.virusafe.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import io.virusafe.service.pushnotification.LogOnlyPushNotificationService;
import io.virusafe.service.pushnotification.PushNotificationService;
import io.virusafe.service.pushnotification.PushNotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

@Configuration
@Slf4j
public class FirebaseConfiguration {

    private static final String COULD_NOT_INITIALIZE_FIREBASE = "Could not initialize Firebase {0}";
    private static final String FIREBASE_INITIALIZED_SUCCESSFULLY = "Firebase initialized successfully";

    /**
     * create FirebaseMessaging bean
     *
     * @param keyPath
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "firebase.push-notifications.enabled", havingValue = "true")
    public PushNotificationService createPushNotificationService(@Value("${firebase.key.path}") final String keyPath) {
        try (InputStream in = Files.newInputStream(Path.of(keyPath))) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(in)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info(FIREBASE_INITIALIZED_SUCCESSFULLY);
            }
        } catch (IOException e) {
            String formattedExceptionMessage = MessageFormat.format(COULD_NOT_INITIALIZE_FIREBASE, e.getMessage());
            log.error(formattedExceptionMessage, e);
            throw new BeanInitializationException(formattedExceptionMessage, e);
        }

        return new PushNotificationServiceImpl(FirebaseMessaging.getInstance());
    }

    /**
     * log only push notification service
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(PushNotificationService.class)
    public PushNotificationService createLogOnlyPushNotificationService() {
        return new LogOnlyPushNotificationService();
    }

}
