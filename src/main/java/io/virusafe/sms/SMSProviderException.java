package io.virusafe.sms;

/**
 * SMS Provider exception
 */
public class SMSProviderException extends RuntimeException {

    /**
     * Create SMSProviderException
     *
     * @param message
     */
    public SMSProviderException(final String message) {
        super(message);
    }

    /**
     * Create SMSProviderException
     *
     * @param message
     * @param cause
     */
    public SMSProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
