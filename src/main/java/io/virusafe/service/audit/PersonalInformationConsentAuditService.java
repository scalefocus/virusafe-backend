package io.virusafe.service.audit;

import io.virusafe.domain.entity.PersonalInformationConsentAction;
import io.virusafe.domain.entity.PersonalInformationConsentAudit;

public interface PersonalInformationConsentAuditService {

    /**
     * Add a new personal information consent audit trail entry for the given user GUID.
     *
     * @param userGuid the user GUID to add an audit trail entry for
     * @param action   the audit action type
     * @return the new audit trail entry
     */
    PersonalInformationConsentAudit addAuditTrailEntry(String userGuid, PersonalInformationConsentAction action);
}
