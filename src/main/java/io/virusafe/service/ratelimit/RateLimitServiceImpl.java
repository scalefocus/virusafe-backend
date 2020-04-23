package io.virusafe.service.ratelimit;

import io.virusafe.domain.entity.RateLimit;
import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.repository.RateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RateLimitServiceImpl implements RateLimitService {
    private final RateLimitRepository rateLimitRepository;

    /**
     * Construct rate limit service.
     *
     * @param rateLimitRepository
     */
    @Autowired
    public RateLimitServiceImpl(final RateLimitRepository rateLimitRepository) {
        this.rateLimitRepository = rateLimitRepository;
    }

    @Override
    public RateLimit findRateLimitByUserAndType(final UserDetails userDetails, final RateLimitType type) {
        Objects.requireNonNull(userDetails);
        Objects.requireNonNull(type);
        return rateLimitRepository.findByUserDetails_IdAndType(userDetails.getId(), type)
                .orElse(RateLimit.builder().userDetails(userDetails).type(type).build());
    }

    @Override
    public RateLimit save(final RateLimit rateLimit) {
        return rateLimitRepository.save(rateLimit);
    }
}
