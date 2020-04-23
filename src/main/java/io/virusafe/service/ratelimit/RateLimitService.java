package io.virusafe.service.ratelimit;

import io.virusafe.domain.entity.RateLimit;
import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;

public interface RateLimitService {
    /**
     * find rate limit by user details and type.
     *
     * @param userDetails
     * @param type
     * @return
     */
    RateLimit findRateLimitByUserAndType(UserDetails userDetails, RateLimitType type);

    /**
     * save rate limit
     *
     * @param rateLimit
     * @return
     */
    RateLimit save(RateLimit rateLimit);
}
