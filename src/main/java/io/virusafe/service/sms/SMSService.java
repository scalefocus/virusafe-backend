package io.virusafe.service.sms;

/**
 * SMS Service
 */
public interface SMSService {

    /**
     * create and send SMS message with PIN
     *
     * @param phoneNumber
     * @param pin
     */
    void sendPinCreationMessage(String phoneNumber, String pin);
}
