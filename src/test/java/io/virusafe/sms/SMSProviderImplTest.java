package io.virusafe.sms;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SMSProviderImplTest {

    private static final String TEST_API_ENDPOINT = "testApiEndpoint";
    private static final String TEST_API_KEY = "testApiKey";
    private static final String TEST_PHONE_NUMBER = "testPhoneNumber";
    private static final String TEST_MESSAGE = "testMessage";

    private static final String DEFAULT_SMS_SERVICE_ID = "123";
    private static final String DEFAULT_SMS_TITLE = "1111";

    @Mock
    private ObjectMapper mockedObjectMapper;

    @Mock
    private RestTemplate mockedRestTemplate;

    @Mock
    private HashGenerator mockedHashGenerator;

    @Test
    public void testSuccessPath() throws JsonProcessingException {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("dummy"));
        when(mockedObjectMapper.readValue(Mockito.anyString(), eq(Map.class)))
                .thenReturn(Collections.singletonMap("meta", Collections.singletonMap("code", HttpStatus.OK.value())));
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
    }

    @Test
    public void testFailCannotDeserializeSMSData() throws JsonProcessingException {
        when(mockedObjectMapper.writeValueAsString(any())).thenThrow(JsonParseException.class);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailNullList() {
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(NullPointerException.class, () -> {
            smsProvider.sendSMS(null);
        });
    }

    @Test
    public void testDoNothingEmptyList() {
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        smsProvider.sendSMS(Collections.emptyList());
        Mockito.verifyNoInteractions(mockedObjectMapper);
    }

    @Test
    public void testFailNoSuchAlgorithmException() throws InvalidKeyException, NoSuchAlgorithmException {
        when(mockedHashGenerator.generateHash(any())).thenThrow(NoSuchAlgorithmException.class);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailInvalidKeyException() throws InvalidKeyException, NoSuchAlgorithmException {
        when(mockedHashGenerator.generateHash(any())).thenThrow(InvalidKeyException.class);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailThrowRestClientException() {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenThrow(RestClientException.class);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailNullResponse() {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(null);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailStatusCodeBadRequest() {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().build());
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailJsonProcessingException() throws JsonProcessingException {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("dummy"));
        when(mockedObjectMapper.readValue(Mockito.anyString(), eq(Map.class))).thenThrow(JsonParseException.class);
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailParseWithoutMeta() throws JsonProcessingException {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("dummy"));
        when(mockedObjectMapper.readValue(Mockito.anyString(), eq(Map.class))).thenReturn(Collections.emptyMap());
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailParseWithMetaWithoutCode() throws JsonProcessingException {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("dummy"));
        when(mockedObjectMapper.readValue(Mockito.anyString(), eq(Map.class)))
                .thenReturn(Collections.singletonMap("meta", Collections.emptyMap()));
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    @Test
    public void testFailParseWithMetaAndCodeButCode401() throws JsonProcessingException {
        when(mockedRestTemplate.postForEntity(eq(TEST_API_ENDPOINT), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("dummy"));
        when(mockedObjectMapper.readValue(Mockito.anyString(), eq(Map.class)))
                .thenReturn(Collections
                        .singletonMap("meta", Collections.singletonMap("code", HttpStatus.UNAUTHORIZED.value())));
        SMSProvider smsProvider = new SMSProviderImpl(mockedRestTemplate, mockedObjectMapper, mockedHashGenerator,
                TEST_API_ENDPOINT,
                TEST_API_KEY);
        Assertions.assertThrows(SMSProviderException.class, () -> {
            smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
        });
    }

    private SMSData createSMSdata() {
        return new SMSData(DEFAULT_SMS_SERVICE_ID, DEFAULT_SMS_TITLE, TEST_PHONE_NUMBER, TEST_MESSAGE);
    }

}