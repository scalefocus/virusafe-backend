package io.virusafe.domain.entity;

/**
 * Enum representing allowed rate limit types.
 */
public enum RateLimitType {
    QUESTIONNAIRE,
    GPS_LOCATION,
    PROXIMITY,
    PIN,
    PERSONAL_INFORMATION,
    PUSH_TOKEN
}
