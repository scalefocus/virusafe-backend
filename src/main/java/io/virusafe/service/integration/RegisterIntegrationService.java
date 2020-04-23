package io.virusafe.service.integration;

import io.virusafe.domain.document.QuestionnaireDocument;

/**
 * Integrate with external consumers
 */
public interface RegisterIntegrationService {
    /**
     * Send questionnaire data to external consumers
     *
     * @param questionnaireDocument
     * @param userGuid
     */
    void sendQuestionnaireData(QuestionnaireDocument questionnaireDocument, String userGuid);
}
