package io.virusafe.repository;

import io.virusafe.domain.entity.PersonalInformationConsentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalInformationConsentAuditRepository extends JpaRepository<PersonalInformationConsentAudit, Long> {
}
