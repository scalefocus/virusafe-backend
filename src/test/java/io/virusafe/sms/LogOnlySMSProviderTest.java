package io.virusafe.sms;

import org.junit.jupiter.api.Test;

import java.util.Collections;

class LogOnlySMSProviderTest {

    private static final String TEST_PHONE_NUMBER = "testPhoneNumber";
    private static final String TEST_MESSAGE = "testMessage";
    private static final String DEFAULT_SMS_SERVICE_ID = "123";
    private static final String DEFAULT_SMS_TITLE = "1234";
    private final LogOnlySMSProvider smsProvider = new LogOnlySMSProvider();

    @Test
    public void testDoesNothing() {
        smsProvider.sendSMS(Collections.singletonList(createSMSdata()));
    }

    private SMSData createSMSdata() {
        return new SMSData(DEFAULT_SMS_SERVICE_ID, DEFAULT_SMS_TITLE, TEST_PHONE_NUMBER, TEST_MESSAGE);
    }
}