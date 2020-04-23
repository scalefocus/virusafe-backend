package io.virusafe.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "rate_limits")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_details_id")
    private UserDetails userDetails;

    @Enumerated(EnumType.STRING)
    private RateLimitType type;

    private LocalDateTime lastUpdateTime;
    private Long bucketCount;

    /**
     * Construct rate limit
     *
     * @param id
     * @param userDetails
     * @param type
     * @param lastUpdateTime
     * @param bucketCount
     */
    @Builder
    public RateLimit(final Long id, final UserDetails userDetails, final RateLimitType type,
                     final LocalDateTime lastUpdateTime,
                     final Long bucketCount) {
        this.id = id;
        this.userDetails = userDetails;
        this.type = type;
        this.lastUpdateTime = lastUpdateTime;
        this.bucketCount = bucketCount;
    }

}
