package io.virusafe.security.advice;

import io.virusafe.configuration.RateLimitConfiguration;
import io.virusafe.domain.entity.RateLimit;
import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.RateLimitTimeoutException;
import io.virusafe.security.advice.calculator.service.RateLimitBucketCalculator;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.ratelimit.RateLimitService;
import io.virusafe.service.userdetails.UserDetailsService;
import org.aspectj.lang.ProceedingJoinPoint;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Abstract rate limit advice
 */
public abstract class RateLimitAdvice {

    private static final long NO_RATE_LIMIT = 0L;

    protected final RateLimitConfiguration rateLimitConfiguration;
    protected final UserDetailsService userDetailsService;
    private final RateLimitService rateLimitService;
    private final Clock systemClock;
    private final RateLimitBucketCalculator rateLimitBucketCalculator;

    /**
     * Construct base rate limit advice
     *
     * @param rateLimitConfiguration
     * @param userDetailsService
     * @param rateLimitService
     * @param systemClock
     * @param rateLimitBucketCalculator
     */
    public RateLimitAdvice(final RateLimitConfiguration rateLimitConfiguration,
                           final UserDetailsService userDetailsService,
                           final RateLimitService rateLimitService,
                           final Clock systemClock,
                           final RateLimitBucketCalculator rateLimitBucketCalculator) {
        this.rateLimitConfiguration = rateLimitConfiguration;
        this.userDetailsService = userDetailsService;
        this.rateLimitService = rateLimitService;
        this.systemClock = systemClock;
        this.rateLimitBucketCalculator = rateLimitBucketCalculator;
    }

    /**
     * rate limit logic
     *
     * @param joinPoint
     * @param userPrincipal
     * @return
     * @throws Throwable
     */
    protected final Object rateLimit(final ProceedingJoinPoint joinPoint,
                                     final UserPrincipal userPrincipal) throws Throwable {
        if (getRateLimit() == NO_RATE_LIMIT || getBucketSize() == NO_RATE_LIMIT) {
            return joinPoint.proceed();
        }

        Optional<UserDetails> dbUser = Optional.empty();
        if (userPrincipal != null) {
            dbUser = userDetailsService.findByUserGuid(userPrincipal.getUserGuid());
        }

        // If user isn't present in the DB, then they don't have any registered attempts and can proceed.
        if (dbUser.isEmpty()) {
            return joinPoint.proceed();
        }
        LocalDateTime requestCalculationTime = LocalDateTime.now(systemClock);

        // Calculate the bucket this request belongs to, based on the existing bucket's bound and count in the DB.
        RateLimit rateLimit = rateLimitBucketCalculator.calculate(
                requestCalculationTime,
                rateLimitService.findRateLimitByUserAndType(dbUser.get(), getRateLimitType()),
                getBucketSize(),
                ChronoUnit.SECONDS);

        // If bucket size has been exceeded, throw an exception and block access to the endpoint.
        if (rateLimit.getBucketCount() > getRateLimit()) {
            throw new RateLimitTimeoutException(
                    ChronoUnit.SECONDS.between(requestCalculationTime, rateLimit.getLastUpdateTime()));
        }

        Object result = joinPoint.proceed();
        // Update bucket information in the DB.
        updateRequestBucket(rateLimit);

        return result;
    }

    private void updateRequestBucket(final RateLimit rateLimit) {
        rateLimitService.save(rateLimit);
    }

    /**
     * Get rate limit
     *
     * @return
     */
    protected abstract long getRateLimit();

    /**
     * get bucket size
     *
     * @return
     */
    protected abstract long getBucketSize();

    /**
     * rate limit type
     *
     * @return
     */
    protected abstract RateLimitType getRateLimitType();

}
