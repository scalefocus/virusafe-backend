package io.virusafe.service.query;

import io.virusafe.domain.query.QuestionnaireQuery;

import java.util.Set;

/**
 * Service that help to search in elasticsearch questionnaire index
 */
public interface QuestionnaireQueryService {
    /**
     * search questionnaire
     *
     * @return
     */
    Set<String> searchInQuestionnaire(QuestionnaireQuery query);
}
