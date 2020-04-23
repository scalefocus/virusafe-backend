package io.virusafe.exception;

/**
 * Push notification exception
 */
public class PushNotificationException extends RuntimeException {
    /**
     * Construct push notification exception
     *
     * @param message
     */
    public PushNotificationException(final String message) {
        super(message);
    }

    /**
     * Construct push notification exception
     *
     * @param message
     * @param cause
     */
    public PushNotificationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
