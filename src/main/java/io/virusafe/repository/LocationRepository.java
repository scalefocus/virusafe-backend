package io.virusafe.repository;

import io.virusafe.domain.document.LocationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocationRepository extends ElasticsearchRepository<LocationDocument, String> {
}
