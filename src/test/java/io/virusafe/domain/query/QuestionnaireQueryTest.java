package io.virusafe.domain.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.dto.QuestionnaireQueryDTO;
import io.virusafe.mapper.QuestionnaireQueryMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionnaireQueryTest {
    private static final String TEST_QUERY_FILE = "queries/questionnaireQuery.json";
    private static final String EXPECTED_GEN_TEST_QUERY_FILE = "queries/expected_result_questionnaireQuery.json";
    private static final String TEST_QUERY_FILE_DIFFERENT_TIMESLOT = "queries/questionnaireQuery_differentTimeSlot.json";
    private static final String EXPECTED_GEN_TEST_QUERY_FILE_DIFFERENT_TIMESLOT = "queries/expected_result_questionnaireQuery_differentTimeSlot.json";
    private static final String TEST_QUERY_FILE_WITHOUT_TIMESLOT = "queries/questionnaireQueryWithoutTimeSlot.json";
    private static final String EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_TIMESLOT = "queries/expected_result_questionnaireQueryWithoutTimeSlot.json";
    private static final String TEST_QUERY_FILE_WITHOUT_POLYGON_POINTS = "queries/questionnaireQueryWithoutPolygonPoints.json";
    private static final String TEST_QUERY_FILE_WITH_EMPTY_POLYGON_POINTS = "queries/questionnaireQueryWithEmptyPolygonPoints.json";
    private static final String EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_POLYGON_POINTS = "queries/expected_result_questionnaireQueryWithoutPolygonPoints.json";
    private static final String TEST_QUERY_FILE_WITHOUT_ANSWERS = "queries/questionnaireQueryWithoutAnswerQuery.json";
    private static final String EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_ANSWERS = "queries/expected_result_questionnaireQueryWithoutAnswerQuery.json";

    private static final QuestionnaireQueryMapper QUESTIONNAIRE_QUERY_MAPPER = new QuestionnaireQueryMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateElasticQuery() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    private SearchQueryGenerator readQuery(final String fileName) throws IOException {
        try (InputStream in = readFile(fileName)) {
            QuestionnaireQueryDTO queryDTO = objectMapper.readValue(in, QuestionnaireQueryDTO.class);
            assertNotNull(queryDTO);
            return QUESTIONNAIRE_QUERY_MAPPER.mapQuestionnaireQueryDTOToQuestionnaireQuery(queryDTO);
        }
    }

    @Test
    public void testCreateElasticQueryWithDifferentTimeslots() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE_DIFFERENT_TIMESLOT);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE_DIFFERENT_TIMESLOT)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    @Test
    public void testCreateElasticQueryWithoutTimeSlot() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE_WITHOUT_TIMESLOT);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_TIMESLOT)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    @Test
    public void testCreateElasticQueryWithoutPolygonPoints() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE_WITHOUT_POLYGON_POINTS);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_POLYGON_POINTS)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    @Test
    public void testCreateElasticQueryWithEmptyPolygonPoints() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE_WITH_EMPTY_POLYGON_POINTS);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_POLYGON_POINTS)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    @Test
    public void testCreateElasticQueryWithoutAnswers() throws IOException {
        SearchQueryGenerator query = readQuery(TEST_QUERY_FILE_WITHOUT_ANSWERS);
        assertNotNull(query);
        try (InputStream expected = readFile(EXPECTED_GEN_TEST_QUERY_FILE_WITHOUT_ANSWERS)) {
            assertEquals(objectMapper.readTree(expected), objectMapper.readTree(query.generateQuery().toString()));
        }
    }

    private InputStream readFile(final String testJson) {
        return Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(testJson));
    }
}