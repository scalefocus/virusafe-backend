package io.virusafe.service.questionnaire;

import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.QuestionDTO;
import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.mapper.QuestionnaireMapper;
import io.virusafe.repository.QuestionRepository;
import io.virusafe.repository.QuestionnaireRepository;
import io.virusafe.service.integration.RegisterIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of questionnaire operations
 */
@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionnaireMapper questionnaireMapper;

    private final QuestionRepository questionRepository;

    private final boolean enabledRegisterDataSend;

    private final List<RegisterIntegrationService> registerIntegrationServices;

    /**
     * Construct a new QuestionnaireService, using the autowired beans.
     *
     * @param questionnaireRepository
     * @param questionnaireMapper
     * @param questionRepository
     * @param enabledRegisterDataSend
     * @param registerIntegrationServices
     */
    public QuestionnaireServiceImpl(final QuestionnaireRepository questionnaireRepository,
                                    final QuestionnaireMapper questionnaireMapper,
                                    final QuestionRepository questionRepository,
                                    @Value("${register.integration.enabled:false}") final boolean enabledRegisterDataSend,
                                    final List<RegisterIntegrationService> registerIntegrationServices) {
        this.questionnaireRepository = questionnaireRepository;
        this.questionnaireMapper = questionnaireMapper;
        this.questionRepository = questionRepository;
        this.enabledRegisterDataSend = enabledRegisterDataSend;
        this.registerIntegrationServices = registerIntegrationServices;
    }

    @Override
    public List<QuestionDTO> getQuestionnaire(final String language) {
        return questionRepository.getAllQuestions(language);
    }

    @Override
    public void postQuestionnaire(final QuestionnairePostDTO questionnairePostDTO, final String userGuid) {
        QuestionnaireDocument questionnaireDocument =
                questionnaireMapper.mapQuestionnaireDTOToQuestionnaire(questionnairePostDTO, userGuid);
        questionnaireRepository.indexWithoutRefresh(questionnaireDocument);
        if (enabledRegisterDataSend && registerIntegrationServices != null) {
            registerIntegrationServices.forEach(registerIntegrationService -> registerIntegrationService
                    .sendQuestionnaireData(questionnaireDocument, userGuid));
            log.info("Finish questionnaire post");
        }
    }

}
