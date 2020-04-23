package io.virusafe.exception;

import org.springframework.security.core.AuthenticationException;

public class UnverifiableRefreshTokenException extends AuthenticationException {

    private static final String ERROR_MESSAGE = "Refresh token can not be verified!";

    /**
     * Construct a new UnverifiableRefreshTokenException with the pre-set message.
     */
    public UnverifiableRefreshTokenException() {
        super(ERROR_MESSAGE);
    }

    /**
     * Construct a new UnverifiableRefreshTokenException with the pre-set message and a given cause.
     *
     * @param throwable the Throwable cause
     */
    public UnverifiableRefreshTokenException(final Throwable throwable) {
        super(ERROR_MESSAGE, throwable);
    }
}
