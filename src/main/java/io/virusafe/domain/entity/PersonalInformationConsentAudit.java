package io.virusafe.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_information_consent_audit")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class PersonalInformationConsentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @EqualsAndHashCode.Include
    private LocalDateTime changedOn;

    @Enumerated(EnumType.STRING)
    private PersonalInformationConsentAction action;

    @EqualsAndHashCode.Include
    private String userGuid;

    /**
     * All args constructor for PersonalInformationConsentAudit.
     * Can be used as a Lombok builder.
     *
     * @param id        the entity's DB ID
     * @param changedOn the time for this audit change
     * @param action    the audit action
     * @param userGuid  the user GUID for this personal information consent change
     */
    @Builder
    public PersonalInformationConsentAudit(final Long id,
                                           final LocalDateTime changedOn,
                                           final PersonalInformationConsentAction action,
                                           final String userGuid) {
        this.id = id;
        this.changedOn = changedOn;
        this.action = action;
        this.userGuid = userGuid;
    }
}
