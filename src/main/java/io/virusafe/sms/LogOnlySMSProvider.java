package io.virusafe.sms;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Dump SMS data to console instead of sending real SMS
 */
@Slf4j
public class LogOnlySMSProvider implements SMSProvider {

    @Override
    public void sendSMS(final List<SMSData> smsDataList) {
        log.info("Console dump SMS {}", smsDataList);
    }
}
