package io.virusafe.exception;

import org.springframework.security.core.AuthenticationException;

import java.text.MessageFormat;

public class UnverifiablePinException extends AuthenticationException {

    private static final String EXCEPTION_MESSAGE = "PIN {0} not valid for phone number {1}";

    /**
     * Construct a new UnverifiablePinException for given PIN and phone number.
     *
     * @param phoneNumber the phone number to mention in the exception message
     * @param pin         the PIN to mention in the exception message
     */
    public UnverifiablePinException(final String phoneNumber, final String pin) {
        super(MessageFormat.format(EXCEPTION_MESSAGE, pin, phoneNumber));
    }
}
