package io.virusafe.domain.dto;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnswerQueryDeserializerTest {
    private static final String TEST_QUERY_FILE = "queries/questionnaireQuery.json";
    private static final String MULTIPLE_ROOTS_QUERY_FILE = "queries/questionnaireQuery_multipleRoots.json";
    private static final String SINGLE_CONDITION_AND_QUERY_FILE = "queries/questionnaireQuery_singleConditionAnd.json";
    private static final String INVALID_QUESTION_DATA_QUERY_FILE = "queries/questionnaireQuery_invalidQuestionData.json";
    private static final String PARTIAL_QUESTION_DATA_QUERY_FILE = "queries/questionnaireQuery_partialQuestionData.json";

    @Test
    public void testRead() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = readFile(TEST_QUERY_FILE)) {
            QuestionnaireQueryDTO query = objectMapper.readValue(in, QuestionnaireQueryDTO.class);
            assertNotNull(query);
            assertAll(
                    () -> assertNotNull(query.getTimeSlot()),
                    () -> assertNull(query.getTimeSlot().getGt()),
                    () -> assertEquals("now-1000m", query.getTimeSlot().getGte()),
                    () -> assertEquals("now", query.getTimeSlot().getLt()),
                    () -> assertNull(query.getTimeSlot().getLte()),

                    () -> assertNotNull(query.getAnswerQuery()),
                    () -> assertNotNull(query.getAnswerQuery().getOperation()),
                    () -> assertTrue(
                            query.getAnswerQuery().getOperation() instanceof QuestionnaireQueryDTO.AndOperationDTO),
                    () -> assertNotNull(((QuestionnaireQueryDTO.AndOperationDTO) query.getAnswerQuery().getOperation())
                            .getSubOperations()),
                    () -> assertEquals(2,
                            ((QuestionnaireQueryDTO.AndOperationDTO) query.getAnswerQuery().getOperation())
                                    .getSubOperations().size()),

                    () -> assertNotNull(query.getPolygonPoints()),
                    () -> assertEquals(4, query.getPolygonPoints().size()),
                    () -> assertEquals(30, query.getPolygonPoints().get(0).getLat()),
                    () -> assertEquals(70, query.getPolygonPoints().get(0).getLon())
            );
        }
    }

    @Test
    public void testReadFailsWithMultipleRootNodes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = readFile(MULTIPLE_ROOTS_QUERY_FILE)) {
            assertThrows(JsonMappingException.class, () ->
                    objectMapper.readValue(in, QuestionnaireQueryDTO.class));
        }
    }

    @Test
    public void testReadFailsWithSingleConditionAnd() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = readFile(SINGLE_CONDITION_AND_QUERY_FILE)) {
            assertThrows(JsonMappingException.class, () ->
                    objectMapper.readValue(in, QuestionnaireQueryDTO.class));
        }
    }

    @Test
    public void testReadFailsWithInvalidQuestionData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = readFile(INVALID_QUESTION_DATA_QUERY_FILE)) {
            assertThrows(JsonMappingException.class, () ->
                    objectMapper.readValue(in, QuestionnaireQueryDTO.class));
        }
    }

    @Test
    public void testReadFailsWithPartialQuestionData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = readFile(PARTIAL_QUESTION_DATA_QUERY_FILE)) {
            assertThrows(JsonMappingException.class, () ->
                    objectMapper.readValue(in, QuestionnaireQueryDTO.class));
        }
    }

    private InputStream readFile(String file) {
        return Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file));
    }
}