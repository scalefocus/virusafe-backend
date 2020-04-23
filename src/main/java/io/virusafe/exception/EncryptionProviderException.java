package io.virusafe.exception;

public class EncryptionProviderException extends RuntimeException {

    /**
     * Construct a new EncryptionProviderException with a given message.
     *
     * @param message the message to include
     */
    public EncryptionProviderException(final String message) {
        super(message);
    }

    /**
     * Construct a new EncryptionProviderException with a given message and cause.
     *
     * @param message the message to include
     * @param cause   the Throwable cause
     */
    public EncryptionProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
