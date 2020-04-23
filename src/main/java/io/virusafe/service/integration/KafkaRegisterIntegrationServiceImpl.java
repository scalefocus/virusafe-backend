package io.virusafe.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.RegisterIntegrationDTO;
import io.virusafe.mapper.RegisterIntegrationMapper;
import io.virusafe.security.encryption.SymmetricEncryptionProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.text.MessageFormat;

/**
 * Integration with the Register using Kafka
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "register.integration.kafka.enabled", havingValue = "true")
public class KafkaRegisterIntegrationServiceImpl implements RegisterIntegrationService {
    private final RegisterIntegrationMapper mapper;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate kafkaTemplate;
    private final SymmetricEncryptionProvider symmetricEncryptionProvider;
    private final String encryptionInitializationVector;
    private final String questionnaireTopicName;

    /**
     * Construct a new KafkaRegisterIntegrationServiceImpl, using the autowired beans.
     *
     * @param mapper
     * @param objectMapper
     * @param kafkaTemplate
     * @param symmetricEncryptionProvider
     * @param encryptionInitializationVector
     * @param questionnaireTopicName
     */
    public KafkaRegisterIntegrationServiceImpl(final RegisterIntegrationMapper mapper,
                                               final ObjectMapper objectMapper,
                                               final KafkaTemplate kafkaTemplate,
                                               @Qualifier(
                                                       "registerEncryption") final SymmetricEncryptionProvider symmetricEncryptionProvider,
                                               @Value("${register.integration.kafka.symmetric.iv}") final String encryptionInitializationVector,
                                               @Value("${spring.kafka.properties.questionnaireTopicName}") final String questionnaireTopicName) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.symmetricEncryptionProvider = symmetricEncryptionProvider;
        this.encryptionInitializationVector = encryptionInitializationVector;
        this.questionnaireTopicName = questionnaireTopicName;
    }

    @Override
    @Async("asyncThreadPoolTaskExecutor")
    public void sendQuestionnaireData(final QuestionnaireDocument questionnaireDocument, final String userGuid) {
        log.info("Send data to Register for user {} using Kafka", userGuid);
        try {
            RegisterIntegrationDTO dto = mapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, userGuid);
            String jsonData = objectMapper.writeValueAsString(dto);
            ListenableFuture<SendResult<String, RegisterIntegrationDTO>> resultFuture = kafkaTemplate
                    .send(questionnaireTopicName,
                            symmetricEncryptionProvider.encrypt(jsonData, encryptionInitializationVector));
            resultFuture.addCallback(result -> successfullySendCallback(result, userGuid),
                    exception -> unsuccessfullySendCallback(exception, userGuid));
        } catch (Exception e) {
            log.error("Error when send data to Register using Kafka!", e);
        }
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void successfullySendCallback(final Object result, final String userGuid) {
        log.info("Successfully send data to Register for user {} using Kafka", userGuid);
    }

    private void unsuccessfullySendCallback(final Throwable exception, final String userGuid) {
        log.info(MessageFormat.format("Unsuccessfully send data to Register for user {0} using Kafka", userGuid), exception);
    }
}
