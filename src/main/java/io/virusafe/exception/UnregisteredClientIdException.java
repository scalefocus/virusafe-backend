package io.virusafe.exception;

public class UnregisteredClientIdException extends SecurityException {

    private static final String EXCEPTION_MESSAGE = "Provided client ID does not match the list of registered clients!";

    /**
     * Construct a new UnregisteredClientIdException with the pre-set message.
     */
    public UnregisteredClientIdException() {
        super(EXCEPTION_MESSAGE);
    }
}
