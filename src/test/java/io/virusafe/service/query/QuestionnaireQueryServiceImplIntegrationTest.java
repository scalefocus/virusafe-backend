package io.virusafe.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.dto.QuestionnaireQueryDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.mapper.QuestionnaireQueryMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestionnaireQueryServiceImplIntegrationTest {
    private static final QuestionnaireQueryMapper QUESTIONNAIRE_QUERY_MAPPER = new QuestionnaireQueryMapper();
    private static final String QUERY = "{  \"answerQuery\": {    \"and\": [      {        \"eq\": {          \"questionId\": 1,          \"answer\": \"True\"        }      },      {        \"or\": [          {            \"eq\": {              \"questionId\": 2,              \"answer\": \"False\"            }          },          {            \"eq\": {              \"questionId\": 3,              \"answer\": \"True\"            }          }        ]      }    ]  }}";

    private static final String USER_GUID = "userGuid";
    private static final int BATCH_SIZE = 1;
    private static final String QUESTIONNAIRE_INDEX = "questionnaire";

    @Test
    @Disabled
    public void testData() throws IOException {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));) {

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            QuestionnaireQuery matchAllQuery = mock(QuestionnaireQuery.class);
            when(matchAllQuery.generateQuery()).thenReturn(searchSourceBuilder);

            QuestionnaireQueryService questionnaireQueryService = new QuestionnaireQueryServiceImpl(client, BATCH_SIZE);
            Set<String> results = questionnaireQueryService.searchInQuestionnaire(matchAllQuery);

            System.out.println(results);
        }

    }

    @Test
    @Disabled
    public void testUsingPaging() throws IOException {

        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));) {

            ObjectMapper mapper = new ObjectMapper();
            QuestionnaireQuery questionnaireQuery = QUESTIONNAIRE_QUERY_MAPPER
                    .mapQuestionnaireQueryDTOToQuestionnaireQuery(mapper.readValue(QUERY,
                            QuestionnaireQueryDTO.class));

            QuestionnaireQueryService questionnaireQueryService = new QuestionnaireQueryServiceImpl(client, BATCH_SIZE);
            Set<String> results = questionnaireQueryService.searchInQuestionnaire(questionnaireQuery);

            System.out.println(results);
        }

    }

    private void searchHits(final SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        float maxScore = hits.getMaxScore();
        System.out.println(MessageFormat
                .format("totalHits: {0} , maxScore: {1}", totalHits, maxScore));
    }

    private void statusData(final SearchResponse searchResponse) {
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();
        System.out.println(MessageFormat
                .format("Status: {0} , took: {1}, terminatedEarly: {2} , timedOut: {3}", status, took,
                        terminatedEarly, timedOut));
    }
}