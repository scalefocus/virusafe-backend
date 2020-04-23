package io.virusafe.domain.entity;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_details")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private Long createdDate;

    @Column(nullable = false, updatable = false)
    private String phoneNumber;

    @EqualsAndHashCode.Include
    private String userGuid;

    private String identificationNumber;

    @Transient
    private String identificationNumberPlain;

    @Enumerated(EnumType.STRING)
    private IdentificationType identificationType;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String preExistingConditions;

    private String pushToken;

    @OneToMany(mappedBy = "userDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<RegistrationPin> registrationPins = new ArrayList<>();

    private String tokenSecret;
    private String refreshToken;

    /**
     * All-args constructor for UserDetails.
     * Can be used as a Lombok builder.
     *
     * @param id                        the entity's DB ID
     * @param phoneNumber               the user's phone number
     * @param userGuid                  the user's GUID
     * @param identificationNumber      the user's encrypted identification number
     * @param identificationNumberPlain the user's non-encrypted identification number. Will not be persisted.
     * @param identificationType        the user's identification type
     * @param age                       the user's age
     * @param gender                    the user's gender
     * @param preExistingConditions     the user's pre-existing conditions
     * @param pushToken                 the user's device's Firebase push token
     * @param registrationPins          the list of all registration PINs for the user
     * @param tokenSecret               the user's JWT token secret, used for additional verification
     * @param refreshToken              the user's hashed refresh token, used for verification
     */
    @Builder
    public UserDetails(final Long id,
                       final String phoneNumber,
                       final String userGuid,
                       final String identificationNumber,
                       final String identificationNumberPlain,
                       final IdentificationType identificationType,
                       final Integer age,
                       final Gender gender,
                       final String preExistingConditions,
                       final String pushToken,
                       final List<RegistrationPin> registrationPins,
                       final String tokenSecret,
                       final String refreshToken) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.userGuid = userGuid;
        this.identificationNumber = identificationNumber;
        this.identificationNumberPlain = identificationNumberPlain;
        this.identificationType = identificationType;
        this.age = age;
        this.gender = gender;
        this.preExistingConditions = preExistingConditions;
        this.pushToken = pushToken;

        if (Objects.nonNull(registrationPins)) {
            registrationPins.forEach(this::addRegistrationPin);
        }
        this.createdDate = System.nanoTime();

        this.tokenSecret = tokenSecret;
        this.refreshToken = refreshToken;
    }

    /**
     * Get all of the user's registration PINs, wrapped in an unmodifiable list.
     *
     * @return the user's registration PINs
     */
    public List<RegistrationPin> getRegistrationPins() {
        return Collections.unmodifiableList(registrationPins);
    }

    /**
     * Add a new registration PIN to the user.
     *
     * @param registrationPin the RegistrationPin to add
     */
    public void addRegistrationPin(final RegistrationPin registrationPin) {
        registrationPins.add(registrationPin);
    }
}
