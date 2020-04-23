package io.virusafe.security.advice.calculator.service;


import io.virusafe.domain.entity.RateLimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RateLimitBucketCalculatorImplTest {

    private static final long DEFAULT_BUCKET_SIZE = 1800L;

    private final RateLimitBucketCalculatorImpl rateLimitBucketCalculator = new RateLimitBucketCalculatorImpl();

    private Clock clock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );

    @Test
    void testNewBucketCreatedWithNullValues() {
        LocalDateTime calculationTime = LocalDateTime.now(clock);
        // Passing nulls so we expect a new bucket set to 30 minutes from the calculation time.
        LocalDateTime existingBucketTime = null;
        Long existingBucketCount = null;

        LocalDateTime expectedBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 30, 0, 0);
        Long expectedBucketCount = 1L;

        doTestCalculator(calculationTime,
                RateLimit.builder().lastUpdateTime(existingBucketTime).bucketCount(existingBucketCount).build(),
                expectedBucketTime, expectedBucketCount);
    }

    @Test
    void testExistingBucketInitializedWhenCountIsNull() {
        LocalDateTime calculationTime = LocalDateTime.now(clock);
        // After calculation time so it's still active.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 0, 0);
        Long existingBucketCount = null;

        LocalDateTime expectedBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 0, 0);
        Long expectedBucketCount = 1L;

        doTestCalculator(calculationTime,
                RateLimit.builder().lastUpdateTime(existingBucketTime).bucketCount(existingBucketCount).build(),
                expectedBucketTime, expectedBucketCount);
    }

    @Test
    void testExistingBucketCountIsIncrementedWhenAfterCalculationTime() {
        LocalDateTime calculationTime = LocalDateTime.now(clock);
        // After calculation time so it's still active.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 0, 0);
        Long existingBucketCount = 2L;

        LocalDateTime expectedBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 11, 0, 0);
        Long expectedBucketCount = 3L;

        doTestCalculator(calculationTime,
                RateLimit.builder().lastUpdateTime(existingBucketTime).bucketCount(existingBucketCount).build(),
                expectedBucketTime, expectedBucketCount);
    }

    @Test
    void testNewBucketIsCreatedWhenExistingBucketTimeIsInThePast() {
        LocalDateTime calculationTime = LocalDateTime.now(clock);
        // Before calculation time so we expect a new bucket to be created.
        LocalDateTime existingBucketTime = LocalDateTime.of(
                2020, 9, 4, 23, 59, 59, 0);
        Long existingBucketCount = 2L;

        // New bucket should be 30 minutes from calculation time.
        LocalDateTime expectedBucketTime = LocalDateTime.of(
                2020, 9, 5, 0, 30, 0, 0);
        Long expectedBucketCount = 1L;

        doTestCalculator(calculationTime,
                RateLimit.builder().lastUpdateTime(existingBucketTime).bucketCount(existingBucketCount).build(),
                expectedBucketTime, expectedBucketCount);
    }

    private void doTestCalculator(final LocalDateTime calculationTime, final RateLimit rateLimit,
                                  final LocalDateTime expectedBucketTime, final Long expectedBucketCount) {
        RateLimit resultRateLimit = rateLimitBucketCalculator
                .calculate(calculationTime, rateLimit, DEFAULT_BUCKET_SIZE, ChronoUnit.SECONDS);
        assertAll(
                () -> assertEquals(expectedBucketTime, resultRateLimit.getLastUpdateTime()),
                () -> assertEquals(expectedBucketCount, resultRateLimit.getBucketCount())
        );
    }
}