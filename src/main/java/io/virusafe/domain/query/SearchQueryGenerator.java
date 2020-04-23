package io.virusafe.domain.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public interface SearchQueryGenerator {

    /**
     * Generate an ElasticSearch SearchSourceBuilder from pre-set conditions.
     *
     * @return the SearchSourceBuilder
     */
    SearchSourceBuilder generateQuery();
}
