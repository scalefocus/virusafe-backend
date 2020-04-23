package io.virusafe.exception;

public class QueryParseException extends RuntimeException {

    /**
     * Construct a new QueryParseException with a given exception message.
     *
     * @param message the message to include
     */
    public QueryParseException(final String message) {
        super(message);
    }
}
