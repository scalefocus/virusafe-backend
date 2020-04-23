package io.virusafe.service.query;

import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.QueryExecuteException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class QuestionnaireQueryServiceImplTest {
    private static final int BATCH_SIZE = 1;

    @Mock
    private RestHighLevelClient client;

    private QuestionnaireQueryService questionnaireQueryService;

    @BeforeEach
    public void setUp() {
        questionnaireQueryService = new QuestionnaireQueryServiceImpl(client, BATCH_SIZE);
    }

    @Test
    public void testNullQuery() {
        assertThrows(NullPointerException.class, () -> questionnaireQueryService.searchInQuestionnaire(null));
    }

    @Test
    public void testQueryExecuteException() throws IOException {
        when(client.count(any(), any())).thenThrow(IOException.class);
        assertThrows(QueryExecuteException.class, () -> questionnaireQueryService.searchInQuestionnaire(
                getMockQuestionnaireQuery()));
    }

    @Test
    public void testReturnCountResponseWithBadRequest() throws IOException {
        CountResponse countResponse = mock(CountResponse.class);
        when(countResponse.status()).thenReturn(RestStatus.BAD_REQUEST);
        when(client.count(any(), any())).thenReturn(countResponse);
        assertThrows(QueryExecuteException.class,
                () -> questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery()));
    }

    @Test
    public void testReturnCountResponseWithNull() throws IOException {
        when(client.count(any(), any())).thenReturn(null);
        assertThrows(QueryExecuteException.class,
                () -> questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery()));
    }

    private QuestionnaireQuery getMockQuestionnaireQuery() {
        QuestionnaireQuery mockQuestionnaireQuery = mock(QuestionnaireQuery.class);
        when(mockQuestionnaireQuery.generateQuery()).thenReturn(mock(SearchSourceBuilder.class));
        return mockQuestionnaireQuery;
    }

    @Test
    public void testReturnSearchResponseWithBadRequest() throws IOException {
        CountResponse countResponse = mock(CountResponse.class);
        when(countResponse.status()).thenReturn(RestStatus.OK);
        when(countResponse.getCount()).thenReturn(1L);
        when(client.count(any(), any())).thenReturn(countResponse);
        SearchResponse response = mock(SearchResponse.class);
        when(response.status()).thenReturn(RestStatus.BAD_REQUEST);
        when(client.search(any(), any(RequestOptions.class))).thenReturn(response);
        assertThrows(QueryExecuteException.class,
                () -> questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery()));
    }

    @Test
    public void testReturnSearchResponseWithNull() throws IOException {
        CountResponse countResponse = mock(CountResponse.class);
        when(countResponse.status()).thenReturn(RestStatus.OK);
        when(countResponse.getCount()).thenReturn(1L);
        when(client.count(any(), any())).thenReturn(countResponse);
        when(client.search(any(), any(RequestOptions.class))).thenReturn(null);
        assertThrows(QueryExecuteException.class,
                () -> questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery()));
    }

    @Test
    public void testReturnLessThanBatchSize() throws IOException {
        CountResponse countResponse = mock(CountResponse.class);
        when(countResponse.status()).thenReturn(RestStatus.OK);
        when(countResponse.getCount()).thenReturn(1L);
        when(client.count(any(), any())).thenReturn(countResponse);

        mockValidSearchResponse();

        questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery());
        verify(client, times(1)).count(any(), any());
        verify(client, times(1)).search(any(), any(RequestOptions.class));
    }

    @Test
    public void testReturnMoreThanBatchSize() throws IOException {
        CountResponse countResponse = mock(CountResponse.class);
        when(countResponse.status()).thenReturn(RestStatus.OK);
        when(countResponse.getCount()).thenReturn(2L);
        when(client.count(any(), any())).thenReturn(countResponse);

        mockValidSearchResponse();

        questionnaireQueryService.searchInQuestionnaire(getMockQuestionnaireQuery());
        verify(client, times(1)).count(any(), any());
        verify(client, times(2)).search(any(), any(RequestOptions.class));
    }

    private void mockValidSearchResponse() throws IOException {
        SearchResponse response = mock(SearchResponse.class);
        when(response.status()).thenReturn(RestStatus.OK);
        SearchHits mockSearchHits = mock(SearchHits.class);
        SearchHit mockSearchHit = mock(SearchHit.class);
        when(mockSearchHits.getHits()).thenReturn(new SearchHit[]{mockSearchHit});
        when(response.getHits()).thenReturn(mockSearchHits);
        when(client.search(any(), any(RequestOptions.class))).thenReturn(response);
    }

}