package io.virusafe.service.questionnaire;

import io.virusafe.domain.dto.QuestionDTO;
import io.virusafe.domain.dto.QuestionnairePostDTO;

import java.util.List;

/**
 * Support Questionnaire operations
 */
public interface QuestionnaireService {
    /**
     * Return all questions in the questionnaire
     *
     * @param language
     * @return
     */
    List<QuestionDTO> getQuestionnaire(final String language);

    /**
     * Save answers on the questionnaire provided by the user
     *
     * @param questionnairePostDTO
     * @param userGuid
     */
    void postQuestionnaire(QuestionnairePostDTO questionnairePostDTO, String userGuid);
}
