package io.virusafe.exception.handler;

import io.jsonwebtoken.JwtException;
import io.virusafe.exception.EncryptionProviderException;
import io.virusafe.exception.InvalidPersonalInformationException;
import io.virusafe.exception.QueryExecuteException;
import io.virusafe.exception.QueryParseException;
import io.virusafe.exception.RateLimitTimeoutException;
import io.virusafe.exception.UnverifiablePinException;
import io.virusafe.exception.UnverifiableRefreshTokenException;
import io.virusafe.exception.model.ErrorDTO;
import io.virusafe.exception.model.FieldValidationErrorDTO;
import io.virusafe.exception.model.ValidationErrorDTO;
import io.virusafe.security.principal.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final int STACK_TRACE_MAX_ELEMENTS_TO_PRINT = 5;
    private static final String STACK_TRACE_PREFIX = "io.virusafe";
    private static final String VALIDATION_FAILED_MESSAGE = "Request arguments validation failed!";
    private static final String EXCEPTION_STACKTRACE_LOG_MESSAGE = "The global exception handler caught the following exception {}, stacktrace: {} user GUID {}";
    private static final String SECURITY_ERROR_MESSAGE = "Invalid header!";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid request!";
    private static final String INVALID_QUERY_REQUEST_MESSAGE = "Invalid query request!";

    /**
     * Default handler to map all unhandled exceptions to HTTP 500.
     *
     * @param genericException the caught exception
     * @return ResponseEntity of HTTP Status 500, containing error details
     */
    @ExceptionHandler({Exception.class})
    public final ResponseEntity<ErrorDTO> handleGenericException(final Exception genericException) {
        ErrorDTO sanitizedErrorDTOO = ErrorDTO.builder().message(INVALID_REQUEST_MESSAGE).build();
        logException(genericException);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(sanitizedErrorDTOO);
    }

    /**
     * Specific handler to also log stacktrace for NPE.
     *
     * @param nullPointerException the caught exception
     * @return ResponseEntity of HTTP Status 500, containing error details
     */

    @ExceptionHandler({NullPointerException.class})
    public final ResponseEntity<ErrorDTO> handleNullPointerException(final NullPointerException nullPointerException) {
        logException(nullPointerException);
        ErrorDTO sanitizedErrorDTO = ErrorDTO.builder().message(INVALID_REQUEST_MESSAGE).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(sanitizedErrorDTO);
    }


    /**
     * Reroute NoSuchElement-class exceptions to HTTP 404.
     *
     * @param noSuchElementException the caught exception
     * @return ResponseEntity of HTTP Status 404, containing error details
     */

    @ExceptionHandler({NoSuchElementException.class})
    public final ResponseEntity<ErrorDTO> handleNoSuchElementException(
            final NoSuchElementException noSuchElementException) {
        logException(noSuchElementException);
        ErrorDTO errorDTO = ErrorDTO.fromExceptionBuilder().exception(noSuchElementException).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorDTO);
    }


    /**
     * Reroute UnverifiablePin exceptions to HTTP 438.
     *
     * @param unverifiablePinException the caught exception
     * @return ResponseEntity of HTTP Status 438, containing error details
     */

    @ExceptionHandler({UnverifiablePinException.class})
    public final ResponseEntity<ErrorDTO> handleUnverifiablePinException(
            final UnverifiablePinException unverifiablePinException) {
        logException(unverifiablePinException);
        ErrorDTO errorDTO = ErrorDTO.fromExceptionBuilder().exception(unverifiablePinException).build();
        return ResponseEntity.status(438)
                .body(errorDTO);
    }

    /**
     * Reroute UnverifiableRefreshToken exceptions to HTTP 403.
     *
     * @param unverifiableRefreshTokenException the caught exception
     * @return ResponseEntity of HTTP Status 403, containing error details
     */

    @ExceptionHandler({UnverifiableRefreshTokenException.class})
    public final ResponseEntity<ErrorDTO> handleUnverifiableRefreshTokenException(
            final UnverifiableRefreshTokenException unverifiableRefreshTokenException) {
        logException(unverifiableRefreshTokenException);
        ErrorDTO errorDTO = ErrorDTO.fromExceptionBuilder().exception(unverifiableRefreshTokenException).build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorDTO);
    }

    /**
     * Reroute Security exceptions to HTTP 401.
     *
     * @param securityException the caught exception
     * @return ResponseEntity of HTTP Status 401, containing error details
     */

    @ExceptionHandler({SecurityException.class})
    public final ResponseEntity<ErrorDTO> handleSecurityException(
            final SecurityException securityException) {
        logException(securityException);
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(SECURITY_ERROR_MESSAGE)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);
    }

    /**
     * Reroute Encryption exceptions to HTTP 401.
     *
     * @param encryptionProviderException the caught exception
     * @return ResponseEntity of HTTP Status 401, containing error details
     */

    @ExceptionHandler({EncryptionProviderException.class})
    public final ResponseEntity<ErrorDTO> handleEncryptionProviderException(
            final EncryptionProviderException encryptionProviderException) {
        logException(encryptionProviderException);
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(SECURITY_ERROR_MESSAGE)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);
    }

    /**
     * Reroute all JWT-related exceptions to HTTP 401.
     *
     * @param jwtException the caught exception
     * @return ResponseEntity of HTTP Status 401, containing error details
     */

    @ExceptionHandler({JwtException.class})
    public final ResponseEntity<ErrorDTO> handleJwtException(
            final JwtException jwtException) {
        logException(jwtException);
        ErrorDTO errorDTO = ErrorDTO.builder()
                .message(SECURITY_ERROR_MESSAGE)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);
    }

    /**
     * Reroute rate limit timeout exceptions to HTTP 429.
     *
     * @param rateLimitTimeoutException the caught exception
     * @return ResponseEntity of HTTP Status 429, containing error details
     */

    @ExceptionHandler({RateLimitTimeoutException.class})
    public final ResponseEntity<ErrorDTO> handleRateLimitTimeoutException(
            final RateLimitTimeoutException rateLimitTimeoutException) {
        logException(rateLimitTimeoutException);
        ErrorDTO errorDTO = ErrorDTO.fromExceptionBuilder().exception(rateLimitTimeoutException).build();
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorDTO);
    }

    /**
     * Reroute JSON parsing exceptions to HTTP 400.
     *
     * @param httpMessageNotReadableException the caught exception
     * @return ResponseEntity of HTTP Status 400, containing error details
     */

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public final ResponseEntity<ErrorDTO> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException httpMessageNotReadableException) {
        logException(httpMessageNotReadableException);
        ErrorDTO errorDTO = ErrorDTO.builder().message(INVALID_REQUEST_MESSAGE).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }

    /**
     * Validation exception exceptions to HTTP 412.
     *
     * @param methodArgumentNotValidException the caught exception
     * @return ResponseEntity of HTTP Status 412, containing error details
     */

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public final ResponseEntity<ValidationErrorDTO> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException methodArgumentNotValidException) {
        logException(methodArgumentNotValidException);
        List<FieldValidationErrorDTO> errors = methodArgumentNotValidException
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(r -> FieldValidationErrorDTO.builder().objectError(r).build())
                .collect(Collectors.toList());

        ValidationErrorDTO errorDTO = ValidationErrorDTO.builder().message(VALIDATION_FAILED_MESSAGE)
                .validationErrors(errors).build();
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(errorDTO);
    }

    /**
     * Reroute InvalidPersonalInformationExceptions to 403.
     *
     * @param invalidPersonalInformationException the caught exception
     * @return ResponseEntity of HTTP Status 403, containing error details
     */

    @ExceptionHandler({InvalidPersonalInformationException.class})
    public final ResponseEntity<ErrorDTO> handleInvalidPersonalInformationException(
            final InvalidPersonalInformationException invalidPersonalInformationException) {
        logException(invalidPersonalInformationException);
        ErrorDTO errorDTO = ErrorDTO.builder().message(INVALID_REQUEST_MESSAGE).build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorDTO);
    }

    /**
     * Reroute QueryExecuteException to 500.
     *
     * @param queryExecuteException the caught exception
     * @return ResponseEntity of HTTP Status 500, containing error details
     */
    @ExceptionHandler({QueryExecuteException.class})
    public final ResponseEntity<ErrorDTO> handleQueryExecuteException(
            final QueryExecuteException queryExecuteException) {
        ErrorDTO sanitizedErrorDTOO = ErrorDTO.builder().message(INVALID_QUERY_REQUEST_MESSAGE).build();
        logException(queryExecuteException);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sanitizedErrorDTOO);
    }

    /**
     * Reroute QueryExecuteException to 500.
     *
     * @param queryParseException the caught exception
     * @return ResponseEntity of HTTP Status 400, containing error details
     */
    @ExceptionHandler({QueryParseException.class})
    public final ResponseEntity<ErrorDTO> handleQueryParseException(final QueryParseException queryParseException) {
        ErrorDTO sanitizedErrorDTOO = ErrorDTO.builder().message(INVALID_QUERY_REQUEST_MESSAGE).build();
        logException(queryParseException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sanitizedErrorDTOO);
    }

    private List<String> filterExceptionStacktrace(final Exception exception) {
        // Print the first N elements of the stacktrace that contain the predefined prefix.
        return Arrays.stream(exception.getStackTrace())
                .filter(e -> e.getClassName().startsWith(STACK_TRACE_PREFIX))
                .map(StackTraceElement::toString)
                .limit(STACK_TRACE_MAX_ELEMENTS_TO_PRINT)
                .collect(Collectors.toList());
    }


    private void logException(final Exception exception) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userGuid = null;
        if (Objects.nonNull(authentication)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                userGuid = ((UserPrincipal) principal).getUserGuid();
            }
        }
        log.error(EXCEPTION_STACKTRACE_LOG_MESSAGE, exception.getMessage(), filterExceptionStacktrace(exception),
                userGuid);
    }

}