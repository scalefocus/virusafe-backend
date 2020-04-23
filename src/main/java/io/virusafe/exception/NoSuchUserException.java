package io.virusafe.exception;

import org.springframework.security.core.AuthenticationException;

import java.text.MessageFormat;

public class NoSuchUserException extends AuthenticationException {

    private static final String EXCEPTION_MESSAGE = "User with GUID {0} does not exist!";
    
    /**
     * Construct a new NoSuchUserException for a given user GUID.
     *
     * @param userGuid the user GUID to mention in the exception message
     */
    public NoSuchUserException(final String userGuid) {
        super(MessageFormat.format(EXCEPTION_MESSAGE, userGuid));
    }
}
