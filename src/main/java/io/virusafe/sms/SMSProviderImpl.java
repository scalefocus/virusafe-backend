package io.virusafe.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Custom SMS provider supported by external company
 */
@Slf4j
public class SMSProviderImpl implements SMSProvider {
    private static final String CANNOT_CONVERT_SMS_DATA_TO_STRING = "Cannot convert SMS data to string";
    private static final String ERROR_INVALID_KEY = "Error when try to create Hmac SHA512 hash";
    private static final String SMS_PROVIDER_INACCESSIBLE = "SMS provider inaccessible URL: {0} ";
    private static final String UNSUPPORTED_SMS_PROVIDER_RESPONSE = "Unsupported SMS provider response";
    private static final String RESPONSE_KEY_META = "meta";
    private static final String RESPONSE_KEY_CODE = "code";
    public static final String X_API_KEY = "x-api-key";
    public static final String X_API_SIGN = "x-api-sign";
    private static final String CANNOT_CALL_SMS_PROVIDER_REST_ENDPOINT = "Cannot call SMS provider REST endpoint!";

    private final String apiEndpoint;
    private final String apiKey;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final HashGenerator hashGenerator;

    /**
     * Create SMS Provider
     *
     * @param restTemplate
     * @param objectMapper
     * @param hashGenerator
     * @param apiEndpoint
     * @param apiKey
     */
    public SMSProviderImpl(final RestTemplate restTemplate, final ObjectMapper objectMapper,
                           final HashGenerator hashGenerator, final String apiEndpoint,
                           final String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.hashGenerator = hashGenerator;
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
    }

    @Override
    public void sendSMS(final List<SMSData> smsDataList) {
        Objects.requireNonNull(smsDataList);
        if (smsDataList.isEmpty()) {
            return;
        }

        log.info("Send {} SMSes", smsDataList.size());
        try {
            String data = objectMapper.writeValueAsString(smsDataList);
            String signature = hashGenerator.generateHash(data);

            HttpEntity<String> request =
                    new HttpEntity<>(data, createHeaders(signature));
            ResponseEntity<String> responseEntityStr = restTemplate.
                    postForEntity(apiEndpoint, request, String.class);

            validateResponse(responseEntityStr);

            log.debug("Dump SMS provider response : {}", responseEntityStr.getBody());
        } catch (JsonProcessingException e) {
            log.error(CANNOT_CONVERT_SMS_DATA_TO_STRING, e);
            throw new SMSProviderException(CANNOT_CONVERT_SMS_DATA_TO_STRING, e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(ERROR_INVALID_KEY, e);
            throw new SMSProviderException(ERROR_INVALID_KEY, e);
        } catch (RestClientException rce) {
            log.error(CANNOT_CALL_SMS_PROVIDER_REST_ENDPOINT, rce);
            throw new SMSProviderException(CANNOT_CALL_SMS_PROVIDER_REST_ENDPOINT, rce);
        }


    }

    private HttpHeaders createHeaders(final String signature) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(X_API_KEY, apiKey);
        headers.add(X_API_SIGN, signature);
        return headers;
    }

    private void validateResponse(final ResponseEntity<String> responseEntityStr) throws JsonProcessingException {
        if (responseEntityStr == null || !responseEntityStr.getStatusCode().is2xxSuccessful()) {
            throw new SMSProviderException(MessageFormat.format(SMS_PROVIDER_INACCESSIBLE, apiEndpoint));
        }
        Map<String, Object> result = objectMapper.readValue(responseEntityStr.getBody(), Map.class);
        if (!result.containsKey(RESPONSE_KEY_META)) {
            throw new SMSProviderException(UNSUPPORTED_SMS_PROVIDER_RESPONSE);
        }
        Map<String, Object> meta = (Map<String, Object>) result.get(RESPONSE_KEY_META);
        if (!Integer.valueOf(HttpStatus.OK.value()).equals(meta.get(RESPONSE_KEY_CODE))) {
            log.debug("Real status code and error message text {}", meta);
            throw new SMSProviderException(UNSUPPORTED_SMS_PROVIDER_RESPONSE);
        }
    }
}
