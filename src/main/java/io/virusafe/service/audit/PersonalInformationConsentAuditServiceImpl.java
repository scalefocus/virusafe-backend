package io.virusafe.service.audit;

import io.virusafe.domain.entity.PersonalInformationConsentAction;
import io.virusafe.domain.entity.PersonalInformationConsentAudit;
import io.virusafe.repository.PersonalInformationConsentAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class PersonalInformationConsentAuditServiceImpl implements PersonalInformationConsentAuditService {

    private final PersonalInformationConsentAuditRepository personalInformationConsentAuditRepository;
    private final Clock systemClock;

    /**
     * Construct a new PersonalInformationConsentAuditServiceImpl,
     * using the autowired PersonalInformationConsentAuditRepository and system clock.
     *
     * @param personalInformationConsentAuditRepository the autowired PersonalInformationConsentAuditRepository bean to use for communicating with the DB
     * @param systemClock                               the system clock to use for audit times
     */
    @Autowired
    public PersonalInformationConsentAuditServiceImpl(final PersonalInformationConsentAuditRepository personalInformationConsentAuditRepository,
                                                      final Clock systemClock) {
        this.personalInformationConsentAuditRepository = personalInformationConsentAuditRepository;
        this.systemClock = systemClock;
    }

    @Override
    public PersonalInformationConsentAudit addAuditTrailEntry(final String userGuid,
                                                              final PersonalInformationConsentAction action) {

        PersonalInformationConsentAudit auditEntry = PersonalInformationConsentAudit.builder()
                .action(action)
                .changedOn(LocalDateTime.now(systemClock))
                .userGuid(userGuid)
                .build();
        personalInformationConsentAuditRepository.save(auditEntry);

        return auditEntry;
    }
}
