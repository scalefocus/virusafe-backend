package io.virusafe.exception;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

public class InvalidPersonalInformationException extends NoSuchElementException {

    private static final String PERSONAL_INFORMATION_EXCEPTION_MESSAGE =
            "Invalid personal information found for user GUID {0}";

    /**
     * Construct a new InvalidPersonalInformationException for a given user GUID.
     *
     * @param userGuid the user GUID to mention in the exception message
     */
    public InvalidPersonalInformationException(final String userGuid) {
        super(MessageFormat.format(PERSONAL_INFORMATION_EXCEPTION_MESSAGE, userGuid));
    }
}
