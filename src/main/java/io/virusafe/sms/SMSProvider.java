package io.virusafe.sms;

import java.util.List;

/**
 * Support creation of SMS
 */
public interface SMSProvider {
    /**
     * Send SMSs as a batch
     *
     * @param smsDataList
     */
    void sendSMS(List<SMSData> smsDataList);
}
