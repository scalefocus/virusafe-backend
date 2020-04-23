package io.virusafe.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "user_registration_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class RegistrationPin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_details_id")
    @EqualsAndHashCode.Include
    @ToString.Exclude
    private UserDetails userDetails;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String pin;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private LocalDateTime validUntil;

    /**
     * All-args constructor for RegistrationPin.
     * Can be used as a Lombok builder.
     *
     * @param id the entity's DB ID
     * @param userDetails the user this PIN is for
     * @param pin the PIN
     * @param validUntil the PIN's validity
     */
    @Builder
    public RegistrationPin(final Long id,
                           final UserDetails userDetails,
                           final String pin,
                           final LocalDateTime validUntil) {
        this.id = id;
        this.userDetails = userDetails;
        this.pin = pin;
        this.validUntil = validUntil;
    }
}
