package io.virusafe.exception.handler;

import io.virusafe.exception.EncryptionProviderException;
import io.virusafe.exception.InvalidPersonalInformationException;
import io.virusafe.exception.InvalidSignatureJwtException;
import io.virusafe.exception.QueryExecuteException;
import io.virusafe.exception.QueryParseException;
import io.virusafe.exception.RateLimitTimeoutException;
import io.virusafe.exception.UnverifiablePinException;
import io.virusafe.exception.UnverifiableRefreshTokenException;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private static final String INVALID_REQUEST_MESSAGE = "Invalid request!";
    private static final String SECURITY_ERROR_MESSAGE = "Invalid header!";
    private static final String VALIDATION_FAILED_MESSAGE = "Request arguments validation failed!";
    private static final String INVALID_QUERY_REQUEST_MESSAGE = "Invalid query request!";
    private static final String USER_GUID = "USER_GUID";
    private static final String STACKTRACE_CLASS_PREFIX = "io.virusafe.package.Class";
    private static final String STACKTRACE_METHOD_NAME = "STACKTRACE_METHOD_NAME";
    private static final String STACKTRACE_FILE_NAME = "STACKTRACE_FILE_NAME";
    private static final String NO_SUCH_ELEMENT_MESSAGE = "NO_SUCH_ELEMENT_MESSAGE";
    private static final String PIN = "PIN";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String UNVERIFIABLE_PIN_MESSAGE = "PIN PIN not valid for phone number PHONE_NUMBER";
    private static final String UNVERIFIABLE_REFRESH_TOKEN_MESSAGE = "Refresh token can not be verified!";
    private static final Integer TIMEOUT_SECONDS = 100;
    private static final String VALIDATION_FIELD = "VALIDATION_FIELD";
    private static final String VALIDATION_OBJECT = "VALIDATION_OBJECT";

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @BeforeEach
    public void setUp() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userGuid(USER_GUID)
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testHandleGenericException() {
        RuntimeException runtimeException = new RuntimeException();
        runtimeException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleGenericException(runtimeException);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleGenericExceptionWithMissingAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
        RuntimeException runtimeException = new RuntimeException();
        runtimeException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleGenericException(runtimeException);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleGenericExceptionWithInvalidAuthenticationPrincipalType() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(USER_GUID, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        RuntimeException runtimeException = new RuntimeException();
        runtimeException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleGenericException(runtimeException);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleNullPointerException() {
        NullPointerException nullPointerException = new NullPointerException();
        nullPointerException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleNullPointerException(nullPointerException);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleNoSuchElementException() {
        NoSuchElementException noSuchElementException = new NoSuchElementException(NO_SUCH_ELEMENT_MESSAGE);
        noSuchElementException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleNoSuchElementException(noSuchElementException);
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertEquals(NO_SUCH_ELEMENT_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleUnverifiablePinException() {
        UnverifiablePinException unverifiablePinException = new UnverifiablePinException(PHONE_NUMBER, PIN);
        unverifiablePinException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleUnverifiablePinException(unverifiablePinException);
        assertAll(
                () -> assertEquals(438, response.getStatusCodeValue()),
                () -> assertEquals(UNVERIFIABLE_PIN_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleUnverifiableRefreshTokenException() {
        UnverifiableRefreshTokenException unverifiableRefreshTokenException =
                new UnverifiableRefreshTokenException();
        unverifiableRefreshTokenException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleUnverifiableRefreshTokenException(unverifiableRefreshTokenException);
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertEquals(UNVERIFIABLE_REFRESH_TOKEN_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleSecurityException() {
        SecurityException securityException = new SecurityException();
        securityException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleSecurityException(securityException);
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals(SECURITY_ERROR_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleEncryptionProviderException() {
        EncryptionProviderException encryptionProviderException = new EncryptionProviderException(
                SECURITY_ERROR_MESSAGE);
        encryptionProviderException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleEncryptionProviderException(encryptionProviderException);
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals(SECURITY_ERROR_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleJwtException() {
        InvalidSignatureJwtException jwtException = new InvalidSignatureJwtException();
        jwtException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleJwtException(jwtException);
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals(SECURITY_ERROR_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleRateLimitTimeoutException() {
        RateLimitTimeoutException rateLimitTimeoutException = new RateLimitTimeoutException(TIMEOUT_SECONDS);
        rateLimitTimeoutException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleRateLimitTimeoutException(rateLimitTimeoutException);
        assertAll(
                () -> assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode()),
                () -> assertEquals(TIMEOUT_SECONDS.toString(), response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException httpMessageNotReadableException = new HttpMessageNotReadableException(
                INVALID_REQUEST_MESSAGE);
        httpMessageNotReadableException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleHttpMessageNotReadableException(httpMessageNotReadableException);
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleQueryExecuteException() {
        QueryExecuteException queryExecuteException = new QueryExecuteException(
                INVALID_REQUEST_MESSAGE);
        queryExecuteException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleQueryExecuteException(queryExecuteException);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertEquals(INVALID_QUERY_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleQueryParseException() {
        QueryParseException queryParseException = new QueryParseException(
                INVALID_REQUEST_MESSAGE);
        queryParseException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleQueryParseException(queryParseException);
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(INVALID_QUERY_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    @Test
    public void testHandleMethodArgumentNotValidException() throws NoSuchMethodException {
        MethodParameter methodParameter =
                new MethodParameter(GlobalExceptionHandler.class.getMethod("handleMethodArgumentNotValidException",
                        MethodArgumentNotValidException.class), 0);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(VALIDATION_OBJECT, VALIDATION_FIELD, VALIDATION_FAILED_MESSAGE);
        ObjectError objectError = mock(ObjectError.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError, objectError));
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(
                methodParameter, bindingResult
        );
        methodArgumentNotValidException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ValidationErrorDTO> response =
                globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);
        assertAll(
                () -> assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode()),
                () -> assertEquals(VALIDATION_FAILED_MESSAGE, response.getBody().getMessage()),
                () -> assertEquals(VALIDATION_FIELD, response.getBody().getValidationErrors().get(0).getFieldName()),
                () -> assertNull(response.getBody().getValidationErrors().get(1).getFieldName())
        );
    }

    @Test
    public void testHandleInvalidPersonalInformationException() {
        InvalidPersonalInformationException invalidPersonalInformationException = new InvalidPersonalInformationException(
                INVALID_REQUEST_MESSAGE);
        invalidPersonalInformationException.setStackTrace(buildDefaultStacktrace());
        ResponseEntity<ErrorDTO> response = globalExceptionHandler
                .handleInvalidPersonalInformationException(invalidPersonalInformationException);
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertEquals(INVALID_REQUEST_MESSAGE, response.getBody().getMessage())
        );
    }

    private StackTraceElement[] buildDefaultStacktrace() {
        StackTraceElement[] stackTraceArray = new StackTraceElement[20];
        for (int i = 0; i < 20; i++) {
            stackTraceArray[i] = new StackTraceElement(STACKTRACE_CLASS_PREFIX + String.valueOf(i),
                    STACKTRACE_METHOD_NAME, STACKTRACE_FILE_NAME, 1);
        }
        return stackTraceArray;
    }
}