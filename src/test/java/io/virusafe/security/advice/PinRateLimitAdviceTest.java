package io.virusafe.security.advice;

import io.virusafe.configuration.RateLimitConfiguration;
import io.virusafe.domain.dto.PinGenerationDTO;
import io.virusafe.domain.entity.RateLimit;
import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.RateLimitTimeoutException;
import io.virusafe.security.advice.calculator.service.RateLimitBucketCalculator;
import io.virusafe.service.ratelimit.RateLimitService;
import io.virusafe.service.userdetails.UserDetailsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinRateLimitAdviceTest {

    private static final String DEFAULT_PHONE_NUMBER = "DEFAULT_PHONE_NUMBER";
    private static final long DEFAULT_BUCKET_SIZE = 1800L;
    private static final long DEFAULT_REQUEST_LIMIT = 3L;
    private static final String USER_GUID = "USER_GUID";
    private static final long USER_ID = 1L;
    private static final long NO_LIMIT = 0L;
    private static final RateLimitType RATE_LIMIT_TYPE = RateLimitType.PIN;
    private static final RateLimit EMPTY_RATE_LIMIT = RateLimit.builder().build();

    @Mock
    private RateLimitConfiguration rateLimitConfiguration;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private RateLimitBucketCalculator rateLimitBucketCalculator;

    private Clock clock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );

    private LocalDateTime calculationTime = LocalDateTime.of(
            2020, 9, 5, 0, 0, 0, 0);

    private final PinGenerationDTO pinGenerationDTO = buildDefaultPinGenerationDTO();

    private PinRateLimitAdvice pinRateLimitAdvice;

    @BeforeEach
    public void setUp() {
        pinRateLimitAdvice = new PinRateLimitAdvice(rateLimitConfiguration, userDetailsService, rateLimitService,
                clock, rateLimitBucketCalculator);
    }

    @Test
    void testAccessAllowedWhenNoUserExists() throws Throwable {

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(DEFAULT_BUCKET_SIZE);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER))
                .thenReturn(Optional.empty());

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void testAccessAllowedWhenExistingBucketTimedOut() throws Throwable {
        // Bucket expired before our current time.
        LocalDateTime expiredBucketTime = LocalDateTime.of(
                2020, 9, 4, 23, 59, 59, 0);
        Long existingBucketCount = 2L;
        // 30 minutes from our base time
        LocalDateTime expectedBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 30, 0, 0);
        Long expectedBucketCount = 1L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(DEFAULT_BUCKET_SIZE);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(dbUser);
        when(rateLimitService.findRateLimitByUserAndType(dbUser.get(), RATE_LIMIT_TYPE))
                .thenReturn(EMPTY_RATE_LIMIT);
        when(rateLimitBucketCalculator.calculate(calculationTime, EMPTY_RATE_LIMIT,
                DEFAULT_BUCKET_SIZE, ChronoUnit.SECONDS))
                .thenReturn(RateLimit.builder().userDetails(dbUser.get()).type(RATE_LIMIT_TYPE)
                        .lastUpdateTime(expectedBucketTime).bucketCount(expectedBucketCount).build());

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void testAccessAllowedForRequestWithinBucket() throws Throwable {

        // Existing bucket is after the system current time.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 4, 0);
        Long existingBucketCount = 2L;

        // Bucket count incremented
        Long expectedBucketCount = 3L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(DEFAULT_BUCKET_SIZE);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(dbUser);
        when(rateLimitService.findRateLimitByUserAndType(dbUser.get(), RATE_LIMIT_TYPE))
                .thenReturn(EMPTY_RATE_LIMIT);
        when(rateLimitBucketCalculator.calculate(calculationTime, EMPTY_RATE_LIMIT,
                DEFAULT_BUCKET_SIZE, ChronoUnit.SECONDS))
                .thenReturn(RateLimit.builder().userDetails(dbUser.get()).type(RATE_LIMIT_TYPE)
                        .lastUpdateTime(existingBucketTime).bucketCount(expectedBucketCount).build());

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }


    @Test
    void testAccessAllowedFirstRequestWithinBucket() throws Throwable {

        // Existing bucket is after the system current time.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 4, 0);
        // No request data saved for the bucket yet.
        Long existingBucketCount = null;

        // Newly initialized bucket
        Long expectedBucketCount = 1L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(DEFAULT_BUCKET_SIZE);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(dbUser);
        when(rateLimitService.findRateLimitByUserAndType(dbUser.get(), RATE_LIMIT_TYPE))
                .thenReturn(EMPTY_RATE_LIMIT);
        when(rateLimitBucketCalculator.calculate(calculationTime, EMPTY_RATE_LIMIT,
                DEFAULT_BUCKET_SIZE, ChronoUnit.SECONDS))
                .thenReturn(RateLimit.builder().userDetails(dbUser.get()).type(RATE_LIMIT_TYPE)
                        .lastUpdateTime(existingBucketTime).bucketCount(expectedBucketCount).build());

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }


    @Test
    void testRateLimitExceptionThrownIfRateLimitExceededForABucket() throws Throwable {

        // Existing bucket is after the system current time.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 4, 0);
        // Rate limit met so next request will exceed it.
        Long existingBucketCount = 3L;
        Long expectedBucketCount = 4L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(DEFAULT_BUCKET_SIZE);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(dbUser);
        when(rateLimitService.findRateLimitByUserAndType(dbUser.get(), RATE_LIMIT_TYPE))
                .thenReturn(EMPTY_RATE_LIMIT);
        when(rateLimitBucketCalculator.calculate(calculationTime, EMPTY_RATE_LIMIT,
                DEFAULT_BUCKET_SIZE, ChronoUnit.SECONDS))
                .thenReturn(RateLimit.builder().userDetails(dbUser.get()).type(RATE_LIMIT_TYPE)
                        .lastUpdateTime(existingBucketTime).bucketCount(expectedBucketCount).build());

        Assertions.assertThrows(RateLimitTimeoutException.class,
                () -> pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO));
    }

    @Test
    void testAccessAllowedWhenNoLimitSet() throws Throwable {
        // Bucket expired before our current time.
        LocalDateTime expiredBucketTime = LocalDateTime.of(
                2020, 9, 4, 23, 59, 59, 0);
        Long existingBucketCount = 2L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(NO_LIMIT);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void testAccessAllowedWhenNoBucketSizeSet() throws Throwable {
        // Bucket expired before our current time.
        LocalDateTime expiredBucketTime = LocalDateTime.of(
                2020, 9, 4, 23, 59, 59, 0);
        Long existingBucketCount = 2L;

        Optional<UserDetails> dbUser = Optional.of(
                UserDetails.builder()
                        .phoneNumber(DEFAULT_PHONE_NUMBER)
                        .userGuid(USER_GUID)
                        .id(USER_ID)
                        .build()
        );

        when(rateLimitConfiguration.getPinRequestLimit()).thenReturn(DEFAULT_REQUEST_LIMIT);
        when(rateLimitConfiguration.getPinRequestBucketSize()).thenReturn(NO_LIMIT);
        when(userDetailsService.findByPhoneNumber(DEFAULT_PHONE_NUMBER)).thenReturn(dbUser);

        pinRateLimitAdvice.pinRateLimit(proceedingJoinPoint, pinGenerationDTO);

        verify(proceedingJoinPoint).proceed();
    }

    private PinGenerationDTO buildDefaultPinGenerationDTO() {
        PinGenerationDTO pinGenerationDTO = new PinGenerationDTO();
        pinGenerationDTO.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        return pinGenerationDTO;
    }
}