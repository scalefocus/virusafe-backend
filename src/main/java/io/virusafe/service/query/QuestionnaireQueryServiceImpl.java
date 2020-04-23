package io.virusafe.service.query;

import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.QueryExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionnaireQueryServiceImpl implements QuestionnaireQueryService {
    private static final String USER_GUID = "userGuid";
    private static final String QUESTIONNAIRE_INDEX = "questionnaire";
    private static final String ELASTICSEARCH_CLIENT_RETURNS_WRONG_RESULTS = "Elasticsearch client returns wrong results!";

    private final RestHighLevelClient restHighLevelClient;
    private final int batchSize;

    /**
     * Construct QuestionnaireQueryServiceImpl using beans
     *
     * @param restHighLevelClient
     * @param batchSize
     */
    @Autowired
    public QuestionnaireQueryServiceImpl(final RestHighLevelClient restHighLevelClient,
                                         @Value("${elasticsearch.read.batch.size:1000}") final int batchSize) {
        this.restHighLevelClient = restHighLevelClient;
        this.batchSize = batchSize;
    }

    @Override
    public Set<String> searchInQuestionnaire(final QuestionnaireQuery query) {
        SearchSourceBuilder searchSourceBuilder = query.generateQuery();

        try {
            long maxCount = getDocumentsCount(searchSourceBuilder);
            int page = 0;
            Set<String> result = new HashSet<>();
            while (maxCount > (batchSize * page)) {
                SearchResponse searchResponse = restHighLevelClient
                        .search(createSearchRequest(searchSourceBuilder, page), RequestOptions.DEFAULT);

                if (searchResponse == null || !RestStatus.OK.equals(searchResponse.status()) || searchResponse
                        .getHits() == null) {
                    throw new QueryExecuteException(ELASTICSEARCH_CLIENT_RETURNS_WRONG_RESULTS);
                }

                Set<String> data = Arrays.stream(searchResponse.getHits().getHits())
                        .map(SearchHit::getSourceAsMap)
                        .map(value -> value.getOrDefault(USER_GUID, ""))
                        .map(Object::toString)
                        .collect(Collectors.toSet());
                result.addAll(data);
                page++;
            }
            return result;
        } catch (IOException ioe) {
            log.error("Cannot read data from elastic", ioe);
            throw new QueryExecuteException("Cannot read data from elastic", ioe);
        }

    }

    private SearchRequest createSearchRequest(final SearchSourceBuilder searchSourceBuilder, final int page) {
        SearchRequest searchRequest = new SearchRequest(QUESTIONNAIRE_INDEX);
        searchSourceBuilder.fetchSource(USER_GUID, null);
        searchSourceBuilder.size(batchSize);
        searchSourceBuilder.from(batchSize * page);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    private long getDocumentsCount(final SearchSourceBuilder searchSourceBuilder) throws IOException {
        CountRequest countRequest = new CountRequest(QUESTIONNAIRE_INDEX);
        countRequest.source(searchSourceBuilder);
        CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        if (countResponse == null || !RestStatus.OK.equals(countResponse.status())) {
            throw new QueryExecuteException(ELASTICSEARCH_CLIENT_RETURNS_WRONG_RESULTS);
        }
        return countResponse.getCount();
    }
}
