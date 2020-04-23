package io.virusafe.security.advice;

import io.virusafe.configuration.RateLimitConfiguration;
import io.virusafe.domain.dto.PinGenerationDTO;
import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.security.advice.calculator.service.RateLimitBucketCalculator;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.ratelimit.RateLimitService;
import io.virusafe.service.userdetails.UserDetailsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Optional;

/**
 * Pin rate limit advice
 */
@Component
@Aspect
public class PinRateLimitAdvice extends RateLimitAdvice {

    /**
     * Construct pin rate limit advice using beans
     *
     * @param rateLimitConfiguration
     * @param userDetailsService
     * @param rateLimitService
     * @param systemClock
     * @param rateLimitBucketCalculator
     */
    @Autowired
    public PinRateLimitAdvice(final RateLimitConfiguration rateLimitConfiguration,
                              final UserDetailsService userDetailsService,
                              final RateLimitService rateLimitService,
                              final Clock systemClock,
                              final RateLimitBucketCalculator rateLimitBucketCalculator) {
        super(rateLimitConfiguration, userDetailsService, rateLimitService, systemClock, rateLimitBucketCalculator);
    }

    /**
     * Around advice over all methods annotated with PinRateLimit
     *
     * @param joinPoint
     * @param pinGenerationDTO
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.virusafe.security.advice.PinRateLimit) && args(pinGenerationDTO,..)")
    public Object pinRateLimit(final ProceedingJoinPoint joinPoint, final PinGenerationDTO pinGenerationDTO)
            throws Throwable {

        Optional<UserDetails> userDetails = this.userDetailsService
                .findByPhoneNumber(pinGenerationDTO.getPhoneNumber());

        UserPrincipal userPrincipal = null;
        // Populate userPrincipal from the DB if this user is already registered so that we can apply rate limit.
        if (userDetails.isPresent()) {
            userPrincipal = UserPrincipal.builder()
                    .userId(userDetails.get().getId())
                    .userGuid(userDetails.get().getUserGuid())
                    .phoneNumber(userDetails.get().getPhoneNumber())
                    .build();
        }

        return rateLimit(joinPoint, userPrincipal);
    }

    @Override
    protected long getRateLimit() {
        return rateLimitConfiguration.getPinRequestLimit();
    }

    @Override
    protected long getBucketSize() {
        return rateLimitConfiguration.getPinRequestBucketSize();
    }

    @Override
    protected RateLimitType getRateLimitType() {
        return RateLimitType.PIN;
    }

}
