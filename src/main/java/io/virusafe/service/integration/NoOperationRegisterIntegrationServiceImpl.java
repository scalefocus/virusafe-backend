package io.virusafe.service.integration;

import io.virusafe.domain.document.QuestionnaireDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "register.integration.enabled", havingValue = "false", matchIfMissing = true)
public class NoOperationRegisterIntegrationServiceImpl implements RegisterIntegrationService {

    @Override
    public void sendQuestionnaireData(final QuestionnaireDocument questionnaireDocument, final String userGuid) {
        log.info("Do NOT send data to Register for user {}", userGuid);
    }
}
