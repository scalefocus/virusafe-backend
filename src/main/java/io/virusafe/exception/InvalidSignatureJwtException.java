package io.virusafe.exception;

import io.jsonwebtoken.JwtException;

public class InvalidSignatureJwtException extends JwtException {

    private static final String EXCEPTION_MESSAGE = "Access token signing algorithm mismatch!";

    /**
     * Construct a new InvalidSignatureJwtException with the pre-set message.
     */
    public InvalidSignatureJwtException() {
        super(EXCEPTION_MESSAGE);
    }
}
