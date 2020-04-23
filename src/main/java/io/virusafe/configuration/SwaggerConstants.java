package io.virusafe.configuration;

public final class SwaggerConstants {

    public static final String CLIENT_ID = "3b3d3b02-e052-4289-827b-6c2f2e2a0430";
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkT24iOiIxNTY3NjUyNDAwIiwidXNlckd1aWQiOiJiYTBkNDdhYS0yYjYwLTQ5OTAtOGU5MS04MDIwYWU3ZTRkMTAiLCJqd3RTZWNyZXQiOiI3NjFiOGEyNi03N2YxLTQxNDEtOWVjNy0wZmZkMmEwZThkNjkiLCJwaG9uZU51bWJlciI6IjU1NTU1NTU1NTUiLCJleHAiOjE1OTkyNzQ4MDB9.DHgcQA0NAa7nySMD02dhhQKrcYP7LvC9AgJYLp-S0nQ";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyR3VpZCI6ImJhMGQ0N2FhLTJiNjAtNDk5MC04ZTkxLTgwMjBhZTdlNGQxMCJ9.fR0oWMwH0ew-TBJSlSae_ZFlB2ehqEWM7MJQ93ybOV8";
    public static final String BEARER_TOKEN = "Bearer " + TOKEN;
    public static final String DEFAULT_TIMESTAMP = "1567652400";
    public static final String DEVICE_UUID = "d70d5c29-30c0-415a-af6a-f7a2ccaa8c76";
    public static final String DEFAULT_DISTANCE = "3.8";
    public static final String DEFAULT_LAT = "42.71";
    public static final String DEFAULT_LNG = "23.32";
    public static final String DEFAULT_IDENTIFICATION_NUMBER = "BG12345678";
    public static final String DEFAULT_IDENTIFICATION_TYPE = "PASSPORT";
    public static final String DEFAULT_AGE = "33";
    public static final String DEFAULT_GENDER = "FEMALE";
    public static final String DEFAULT_EXISTING_CONDITIONS = "Pre-existing conditions";
    public static final String DEFAULT_PHONE_NUMBER = "5555555555";
    public static final String DEFAULT_PIN = "000000";
    public static final String PUSH_TOKEN = "9077218c-ad45-4ac0-b2d3-0a566f48b9b7";
    public static final String LANGUAGE = "en";

    public static final String UNAUTHORIZED_MESSAGE = "Access unauthorized";
    public static final String ACCESS_FORBIDDEN_MESSAGE = "Access forbidden";
    public static final String USER_NOT_FOUND_MESSAGE = "No corresponding user was found";
    public static final String FAILED_VALIDATION_MESSAGE = "Request argument validation has failed";
    public static final String RATE_LIMIT_EXCEEDED_MESSAGE = "Request rate limit has been exceeded";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected server error has occurred";
    public static final String SUBMITTED_SUCCESSFULLY_MESSAGE = "Submitted successfully";

    private SwaggerConstants() {
    }
}
