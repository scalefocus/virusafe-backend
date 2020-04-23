package io.virusafe.service.sms;

import io.virusafe.configuration.SMSConfiguration;
import io.virusafe.sms.SMSData;
import io.virusafe.sms.SMSProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collections;

/**
 * SMS service implementation
 */
@Component
public class SMSServiceImpl implements SMSService {

    private final SMSProvider smsProvider;

    private final SMSConfiguration smsConfiguration;

    /**
     * Construct a new SMSService, using the autowired beans.
     *
     * @param smsProvider
     * @param smsConfiguration
     */
    @Autowired
    public SMSServiceImpl(final SMSProvider smsProvider, final SMSConfiguration smsConfiguration) {
        this.smsProvider = smsProvider;
        this.smsConfiguration = smsConfiguration;
    }

    @Override
    public void sendPinCreationMessage(final String phoneNumber, final String pin) {

        SMSData smsDatum = SMSData.builder()
                .title(smsConfiguration.getTitle())
                .serviceId(smsConfiguration.getServiceId())
                .phoneNumber(phoneNumber)
                .message(MessageFormat.format(smsConfiguration.getMessagePattern(), pin)).build();

        smsProvider.sendSMS(Collections.singletonList(smsDatum));
    }
}
