package io.virusafe.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.sms.HashGenerator;
import io.virusafe.sms.HmacHashGenerator;
import io.virusafe.sms.LogOnlySMSProvider;
import io.virusafe.sms.SMSProvider;
import io.virusafe.sms.SMSProviderImpl;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SMSProviderConfiguration {

    /**
     * Provide an SMSProvider bean, configured to send messages using an external provider.
     * Configurable by the sms.provider.api-endpoint property.
     *
     * @param smsProviderProperties the autowired configuration properties to use for integrating with the external provider
     * @param objectMapper          the autowired ObjectMapper to use for message conversions
     * @param restTemplate          the autowired RestTemplate to use for REST communication with the external provider
     * @return the external provider SMSProvider bean
     */
    @Bean
    @ConditionalOnProperty(value = "sms.provider.api-endpoint")
    public SMSProvider smsProvider(final SMSProviderProperties smsProviderProperties,
                                               final ObjectMapper objectMapper,
                                               final RestTemplate restTemplate) {
        HashGenerator hashGenerator = new HmacHashGenerator(smsProviderProperties.getApiSecret());
        return new SMSProviderImpl(restTemplate, objectMapper, hashGenerator, smsProviderProperties.getApiEndpoint(),
                smsProviderProperties.getApiKey());
    }

    /**
     * Provide a new RestTemplate bean to be used for REST communication with the SMS provider.
     * Uses default RestTemplate configuration.
     *
     * @return the RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Configuration
    @ConditionalOnProperty(value = "sms.provider.api-endpoint")
    @ConfigurationProperties(prefix = "sms.provider")
    @Data
    public static class SMSProviderProperties {
        private String apiEndpoint;
        private String apiKey;
        private String apiSecret;
    }

    /**
     * Provide an SMSProviderBean that does not send messages and only logs incoming requests.
     * This is the fallback bean to use when no external provider configuration is present.
     *
     * @return the log-only SMSProvider bean
     */
    @Bean
    @ConditionalOnMissingBean(SMSProvider.class)
    public SMSProvider smsProviderConsole() {
        return new LogOnlySMSProvider();
    }

}