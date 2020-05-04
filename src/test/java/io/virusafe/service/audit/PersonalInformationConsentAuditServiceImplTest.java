package io.virusafe.service.audit;

import io.virusafe.domain.entity.PersonalInformationConsentAction;
import io.virusafe.domain.entity.PersonalInformationConsentAudit;
import io.virusafe.repository.PersonalInformationConsentAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PersonalInformationConsentAuditServiceImplTest {

    private static final String USER_GUID = "USER_GUID";
    private static final LocalDateTime CALCULATION_TIME = LocalDateTime.of(2020, 9, 5, 0, 0, 0, 0);
    private Clock clock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );

    @Mock
    private PersonalInformationConsentAuditRepository personalInformationConsentAuditRepository;

    private PersonalInformationConsentAuditServiceImpl personalInformationConsentAuditService;

    @BeforeEach
    public void setUp() {
        personalInformationConsentAuditService = new PersonalInformationConsentAuditServiceImpl(
                personalInformationConsentAuditRepository, clock);
    }

    @Test
    public void testAddAuditTrailEntry() {
        personalInformationConsentAuditService.addAuditTrailEntry(USER_GUID, PersonalInformationConsentAction.GRANTED);

        PersonalInformationConsentAudit expectedAuditTrail = PersonalInformationConsentAudit.builder()
                .userGuid(USER_GUID)
                .action(PersonalInformationConsentAction.GRANTED)
                .changedOn(CALCULATION_TIME)
                .build();
        verify(personalInformationConsentAuditRepository, times(1)).save(expectedAuditTrail);
    }
}