package io.virusafe.service.sms;

import io.virusafe.configuration.SMSConfiguration;
import io.virusafe.sms.SMSData;
import io.virusafe.sms.SMSProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SMSServiceImplTest {

    private static final String TITLE = "TITLE";
    private static final String SERVICE_ID = "SERVICE_ID";
    private static final String PIN_PATTERN = "PIN_PATTERN {0}";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String PIN = "PIN";
    private static final String PIN_MESSAGE = "PIN_PATTERN PIN";
    @Mock
    private SMSProvider smsProvider;
    @Mock
    private SMSConfiguration smsConfiguration;

    private SMSServiceImpl smsService;

    @BeforeEach
    public void setUp() {
        smsService = new SMSServiceImpl(smsProvider, smsConfiguration);
    }

    @Test
    public void testSendPinCreationMessage() {
        when(smsConfiguration.getTitle()).thenReturn(TITLE);
        when(smsConfiguration.getServiceId()).thenReturn(SERVICE_ID);
        when(smsConfiguration.getMessagePattern()).thenReturn(PIN_PATTERN);

        smsService.sendPinCreationMessage(PHONE_NUMBER, PIN);
        SMSData smsDatum = SMSData.builder()
                .phoneNumber(PHONE_NUMBER)
                .serviceId(SERVICE_ID)
                .title(TITLE)
                .message(PIN_MESSAGE)
                .build();
        verify(smsProvider, times(1)).sendSMS(Collections.singletonList(smsDatum));
    }
}