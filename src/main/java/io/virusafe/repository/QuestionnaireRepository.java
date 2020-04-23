package io.virusafe.repository;

import io.virusafe.domain.document.QuestionnaireDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionnaireRepository extends ElasticsearchRepository<QuestionnaireDocument, String> {
}
