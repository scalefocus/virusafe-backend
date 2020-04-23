package io.virusafe.security.advice.calculator.service;

import io.virusafe.domain.entity.RateLimit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Rate limit calculator
 */
public interface RateLimitBucketCalculator {

    /**
     * For a given time, calculate whether it should be added to an existing bucket or if a new time bucket
     * should be created.
     *
     * @param calculationTime  the time to check
     * @param rateLimit        the rate limit
     * @param bucketSize       the bucket size to use when initializing time buckets
     * @param bucketChronoUnit the chrono unit to use when sizing new time buckets
     * @return the calculated bucket's upper bound and request size.
     */
    RateLimit calculate(LocalDateTime calculationTime,
                        RateLimit rateLimit,
                        Long bucketSize,
                        ChronoUnit bucketChronoUnit);
}
