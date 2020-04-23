package io.virusafe.exception;

public class RateLimitTimeoutException extends RuntimeException {

    /**
     * Construct a new RateLimitTimeoutException for a given number of seconds.
     *
     * @param timeoutSeconds the timeout seconds to mention in the exception message
     */
    public RateLimitTimeoutException(final long timeoutSeconds) {
        super(String.valueOf(timeoutSeconds));
    }
}
