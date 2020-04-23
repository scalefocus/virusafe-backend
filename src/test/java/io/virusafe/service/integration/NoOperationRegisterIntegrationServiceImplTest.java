package io.virusafe.service.integration;

import io.virusafe.domain.document.QuestionnaireDocument;
import org.junit.jupiter.api.Test;

class NoOperationRegisterIntegrationServiceImplTest {

    private static final String USER_GUID = "USER_GUID";

    private final NoOperationRegisterIntegrationServiceImpl registerIntegrationService =
            new NoOperationRegisterIntegrationServiceImpl();

    QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
            .userGuid(USER_GUID)
            .build();
    @Test
    public void testDoesNothing() {
        registerIntegrationService.sendQuestionnaireData(questionnaireDocument, USER_GUID);
        // Nothing should happen.
    }
}