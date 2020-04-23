package io.virusafe.service;


import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.mapper.QuestionnaireMapper;
import io.virusafe.repository.QuestionRepository;
import io.virusafe.repository.QuestionnaireRepository;
import io.virusafe.service.integration.RegisterIntegrationService;
import io.virusafe.service.questionnaire.QuestionnaireServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceImplTest {
    private static final String LANGUAGE = "LANGUAGE";
    private static final String USER_GUID = "USER_GUID";
    @Mock
    private QuestionnaireRepository questionnaireRepository;
    @Mock
    private QuestionnaireMapper questionnaireMapper;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private RegisterIntegrationService registerIntegrationService;

    private QuestionnairePostDTO questionnairePostDTO = new QuestionnairePostDTO();

    @Test
    public void testGetQuestionnaireProxiesToRepository() {
        QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(questionnaireRepository,
                questionnaireMapper, questionRepository, true, null);
        questionnaireService.getQuestionnaire(LANGUAGE);
        verify(questionRepository, times(1)).getAllQuestions(LANGUAGE);
    }

    @Test
    public void testPostQuestionnaireInvokesRegistrationService() {
        QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(questionnaireRepository,
                questionnaireMapper, questionRepository,
                true, Collections.singletonList(registerIntegrationService));
        questionnaireService.postQuestionnaire(questionnairePostDTO, USER_GUID);

        verify(questionnaireRepository, times(1)).indexWithoutRefresh(any());
        verify(registerIntegrationService, times(1)).sendQuestionnaireData(any(), eq(USER_GUID));
    }

    @Test
    public void testPostQuestionnaireDoesntInvokeRegistrationServiceWhenDisabled() {
        QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(questionnaireRepository,
                questionnaireMapper, questionRepository,
                false, Collections.singletonList(registerIntegrationService));
        questionnaireService.postQuestionnaire(questionnairePostDTO, USER_GUID);

        verify(questionnaireRepository, times(1)).indexWithoutRefresh(any());
        verify(registerIntegrationService, times(0)).sendQuestionnaireData(any(), eq(USER_GUID));
    }

    @Test
    public void testPostQuestionnaireSucceedsWhenRegisterServiceListIsNull() {
        QuestionnaireServiceImpl questionnaireService = new QuestionnaireServiceImpl(questionnaireRepository,
                questionnaireMapper, questionRepository,
                true, null);
        questionnaireService.postQuestionnaire(questionnairePostDTO, USER_GUID);

        verify(questionnaireRepository, times(1)).indexWithoutRefresh(any());
        verify(registerIntegrationService, times(0)).sendQuestionnaireData(any(), eq(USER_GUID));
    }
}