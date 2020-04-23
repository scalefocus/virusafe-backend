package io.virusafe.exception;

import io.jsonwebtoken.JwtException;

import java.text.MessageFormat;

public class InvalidClaimJwtException extends JwtException {
    private static final String EXCEPTION_MESSAGE = "Access token claim mismatch {0}!";

    /**
     * Construct a new InvalidClaimJwtException for a given claim field.
     *
     * @param claimField the claim field to mention in the exception message
     */
    public InvalidClaimJwtException(final String claimField) {
        super(MessageFormat.format(EXCEPTION_MESSAGE, claimField));
    }

    /**
     * Construct a new InvalidClaimJwtException for a given claim field and cause.
     *
     * @param claimField the claim field to mention in the exception message
     * @param throwable  the Throwable cause
     */
    public InvalidClaimJwtException(final String claimField, final Throwable throwable) {
        super(MessageFormat.format(EXCEPTION_MESSAGE, claimField), throwable);
    }
}
