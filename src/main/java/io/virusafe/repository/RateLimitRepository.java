package io.virusafe.repository;

import io.virusafe.domain.entity.RateLimit;
import io.virusafe.domain.entity.RateLimitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, Long> {

    /**
     * Find rate limit by userId and type
     *
     * @param userId
     * @param type
     * @return
     */
    Optional<RateLimit> findByUserDetails_IdAndType(Long userId, RateLimitType type);

}
