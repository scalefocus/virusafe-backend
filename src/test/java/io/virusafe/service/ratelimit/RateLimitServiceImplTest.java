package io.virusafe.service.ratelimit;

import io.virusafe.domain.entity.RateLimitType;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.repository.RateLimitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceImplTest {

    private static final RateLimitType DEFAULT_RATE_LIMIT = RateLimitType.PIN;
    private static final UserDetails USER_DETAILS = UserDetails.builder().build();
    @Mock
    private RateLimitRepository rateLimitRepository;

    private RateLimitService rateLimitService;

    @BeforeEach
    public void setUp() {
        rateLimitService = new RateLimitServiceImpl(rateLimitRepository);
    }

    @Test
    void findRateLimitByUserAndType_NullUserDetails() {
        Assertions.assertThrows(NullPointerException.class,
                () -> rateLimitService.findRateLimitByUserAndType(null, DEFAULT_RATE_LIMIT));
    }

    @Test
    void findRateLimitByUserAndType_NullType() {
        Assertions.assertThrows(NullPointerException.class,
                () -> rateLimitService.findRateLimitByUserAndType(USER_DETAILS, null));
    }

    @Test
    void findRateLimitByUserAndType() {
        rateLimitService.findRateLimitByUserAndType(USER_DETAILS, DEFAULT_RATE_LIMIT);
        verify(rateLimitRepository, times(1)).findByUserDetails_IdAndType(any(), any());
    }

    @Test
    void save() {
        rateLimitService.save(null);
        verify(rateLimitRepository, times(1)).save(any());
    }
}