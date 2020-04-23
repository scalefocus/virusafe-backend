package io.virusafe.exception;

public class QueryExecuteException extends RuntimeException {

    /**
     * Construct a new QueryParseException with a given exception message.
     *
     * @param message the message to include
     */
    public QueryExecuteException(final String message) {
        super(message);
    }

    /**
     * Construct a new QueryParseException with a given exception message.
     *
     * @param message the message to include
     * @param cause
     */
    public QueryExecuteException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
