package io.virusafe.security.advice.calculator.service;

import io.virusafe.domain.entity.RateLimit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Default rate limit calculator
 */
@Service
public class RateLimitBucketCalculatorImpl implements RateLimitBucketCalculator {

    @Override
    public RateLimit calculate(final LocalDateTime calculationTime,
                               final RateLimit rateLimit,
                               final Long bucketSize,
                               final ChronoUnit bucketChronoUnit) {

        LocalDateTime bucketTime = calculationTime;
        long bucketCount = 1L;

        // If no bucket time has been passed or the bucket is no longer valid, create new bucket (with count 1).
        if (Objects.isNull(rateLimit.getLastUpdateTime()) || rateLimit.getLastUpdateTime().isBefore(bucketTime)) {
            bucketTime = bucketTime.plus(bucketSize, bucketChronoUnit);
        } else {
            bucketTime = rateLimit.getLastUpdateTime();
            // If existing bucket time is set but has no count, initialize it with count 1.
            // Otherwise, add 1 to the existing count.
            if (Objects.nonNull(rateLimit.getBucketCount())) {
                bucketCount += rateLimit.getBucketCount();
            }
        }
        rateLimit.setLastUpdateTime(bucketTime);
        rateLimit.setBucketCount(bucketCount);
        return rateLimit;
    }
}
