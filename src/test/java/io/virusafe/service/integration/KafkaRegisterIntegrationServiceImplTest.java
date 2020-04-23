package io.virusafe.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.RegisterIntegrationDTO;
import io.virusafe.mapper.RegisterIntegrationMapper;
import io.virusafe.security.encryption.SymmetricEncryptionProvider;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaRegisterIntegrationServiceImplTest {

    private static final String TOPIC_NAME = "TOPIC_NAME";
    private static final String USER_GUID = "USER_GUID";
    private static final String IDENTIFICATION_NUMBER = "IDENTIFICATION_NUMBER";
    private static final int PARTITION = 1;
    private static final long BASE_OFFSET = 0L;
    private static final long RELATIVE_OFFSET = 0L;
    private static final long TIMESTAMP = 0L;
    private static final long CHECKSUM = 0L;
    private static final int SERIALIZED_KEY_SIZE = 0;
    private static final int SERIALIZED_VALUE_SIZE = 0;
    private static final String INITIALIZATION_VECTOR = "INITIALIZATION_VECTOR";
    private static final String MAPPED_MESSAGE = "MAPPED_MESSAGE";
    public static final String ENCRYPTED_MESSAGE = "ENCRYPTED_MESSAGE";
    @Mock
    private RegisterIntegrationMapper registerIntegrationMapper;
    @Mock
    private KafkaTemplate kafkaTemplate;
    @Mock
    private SymmetricEncryptionProvider symmetricEncryptionProvider;
    @Mock
    private ObjectMapper objectMapper;

    private KafkaRegisterIntegrationServiceImpl registerIntegrationService;

    QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
            .userGuid(USER_GUID)
            .build();
    RegisterIntegrationDTO registerIntegrationDTO = RegisterIntegrationDTO.builder()
            .identificationNumber(IDENTIFICATION_NUMBER)
            .build();

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        when(registerIntegrationMapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, USER_GUID))
                .thenReturn(registerIntegrationDTO);
        when(objectMapper.writeValueAsString(registerIntegrationDTO)).thenReturn(MAPPED_MESSAGE);
        when(symmetricEncryptionProvider.encrypt(MAPPED_MESSAGE, INITIALIZATION_VECTOR))
                .thenReturn(ENCRYPTED_MESSAGE);
        registerIntegrationService = new KafkaRegisterIntegrationServiceImpl(
                registerIntegrationMapper, objectMapper, kafkaTemplate,
                symmetricEncryptionProvider, INITIALIZATION_VECTOR, TOPIC_NAME);
    }

    @Test
    public void testSendQuestionnaireData() {
        ProducerRecord<String, RegisterIntegrationDTO> producerRecord = new ProducerRecord<>(
                TOPIC_NAME, registerIntegrationDTO);
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition(TOPIC_NAME, PARTITION),
                BASE_OFFSET,
                RELATIVE_OFFSET,
                TIMESTAMP,
                CHECKSUM,
                SERIALIZED_KEY_SIZE,
                SERIALIZED_VALUE_SIZE
        );
        SendResult<String, RegisterIntegrationDTO> sendResult = new SendResult<>(producerRecord, recordMetadata);
        SettableListenableFuture<SendResult<String, RegisterIntegrationDTO>> resultFuture =
                new SettableListenableFuture<>();
        resultFuture.set(sendResult);
        when(kafkaTemplate.send(TOPIC_NAME, ENCRYPTED_MESSAGE)).thenReturn(resultFuture);

        registerIntegrationService.sendQuestionnaireData(questionnaireDocument, USER_GUID);
        verify(kafkaTemplate, times(1)).send(TOPIC_NAME, ENCRYPTED_MESSAGE);
    }

    @Test
    public void testSendQuestionnaireDataCallbackExceptionConsumed() {
        SettableListenableFuture<SendResult<String, RegisterIntegrationDTO>> resultFuture =
                new SettableListenableFuture<>();
        resultFuture.setException(new RuntimeException());
        when(kafkaTemplate.send(TOPIC_NAME, ENCRYPTED_MESSAGE)).thenReturn(resultFuture);

        registerIntegrationService.sendQuestionnaireData(questionnaireDocument, USER_GUID);
        verify(kafkaTemplate, times(1)).send(TOPIC_NAME, ENCRYPTED_MESSAGE);
    }


    @Test
    public void testSendQuestionnaireDataTemplateExceptionConsumed() {
        when(kafkaTemplate.send(TOPIC_NAME, ENCRYPTED_MESSAGE))
                .thenThrow(new RuntimeException());

        registerIntegrationService.sendQuestionnaireData(questionnaireDocument, USER_GUID);
        verify(kafkaTemplate, times(1)).send(TOPIC_NAME, ENCRYPTED_MESSAGE);
    }
}