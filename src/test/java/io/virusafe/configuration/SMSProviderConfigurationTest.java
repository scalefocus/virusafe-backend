package io.virusafe.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.sms.LogOnlySMSProvider;
import io.virusafe.sms.SMSProvider;
import io.virusafe.sms.SMSProviderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SMSProviderConfigurationTest {

    private final SMSProviderConfiguration smsProviderConfiguration = new SMSProviderConfiguration();

    @Test
    public void testCanCreateLogOnlySMSProvider() {
        SMSProvider smsProvider = smsProviderConfiguration.smsProviderConsole();
        assertTrue(smsProvider instanceof LogOnlySMSProvider);
    }

    @Test
    public void testCanCreateRealSMSProvider() {
        SMSProvider smsProvider = smsProviderConfiguration.smsProvider(
                mock(SMSProviderConfiguration.SMSProviderProperties.class),
                mock(ObjectMapper.class),
                mock(RestTemplate.class));
        assertTrue(smsProvider instanceof SMSProviderImpl);
    }

}