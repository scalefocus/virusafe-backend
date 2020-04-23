package io.virusafe.repository;

import io.virusafe.domain.document.LocationProximityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocationProximityRepository extends ElasticsearchRepository<LocationProximityDocument, String> {
}
