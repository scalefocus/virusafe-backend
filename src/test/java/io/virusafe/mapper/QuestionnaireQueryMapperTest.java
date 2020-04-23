package io.virusafe.mapper;

import io.virusafe.domain.dto.QuestionnaireQueryDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.QueryParseException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionnaireQueryMapperTest {

    private static final int POINTS_COUNT = 4;
    private static final String QUESTION_ID = "1";
    private static final String TEST_ANSWER = "testAnswer";
    private static final String TEST_GT = "testGT";
    private static final String TEST_GTE = "testGTE";
    private static final String TEST_LT = "testLT";
    private static final String TEST_LTE = "testLTE";
    private static final String DEFAULT_MIN_MATCH = "1";

    @Test
    void mapQuestionnaireQueryDTOToQuestionnaireQuery_TimeSlot() {
        QuestionnaireQueryDTO questionnaireQueryDTO = new QuestionnaireQueryDTO();
        questionnaireQueryDTO.setTimeSlot(
                new QuestionnaireQueryDTO.TimeSlotDTO(TEST_GT, TEST_GTE, TEST_LT, TEST_LTE));
        QuestionnaireQueryMapper questionnaireQueryMapper = new QuestionnaireQueryMapper();
        QuestionnaireQuery questionnaireQuery = questionnaireQueryMapper
                .mapQuestionnaireQueryDTOToQuestionnaireQuery(questionnaireQueryDTO);
        assertNotNull(questionnaireQuery);
        assertNotNull(questionnaireQuery.getTimeSlot());
        final QuestionnaireQuery.TimeSlot timeSlot = questionnaireQuery.getTimeSlot();
        assertAll(
                () -> assertEquals(questionnaireQueryDTO.getTimeSlot().getGt(), timeSlot.getGt()),
                () -> assertEquals(questionnaireQueryDTO.getTimeSlot().getGte(), timeSlot.getGte()),
                () -> assertEquals(questionnaireQueryDTO.getTimeSlot().getLt(), timeSlot.getLt()),
                () -> assertEquals(questionnaireQueryDTO.getTimeSlot().getLte(), timeSlot.getLte())
        );
    }

    @Test
    void mapQuestionnaireQueryDTOToQuestionnaireQuery_PolygonPoints() {
        QuestionnaireQueryDTO questionnaireQueryDTO = new QuestionnaireQueryDTO();
        questionnaireQueryDTO.setPolygonPoints(
                createPolygonPoints());
        QuestionnaireQueryMapper questionnaireQueryMapper = new QuestionnaireQueryMapper();
        QuestionnaireQuery questionnaireQuery = questionnaireQueryMapper
                .mapQuestionnaireQueryDTOToQuestionnaireQuery(questionnaireQueryDTO);
        assertNotNull(questionnaireQuery);
        assertNotNull(questionnaireQuery.getPolygonPoints());
        final List<QuestionnaireQuery.Point> polygonPoints = questionnaireQuery.getPolygonPoints();
        assertAll(
                () -> assertEquals(POINTS_COUNT, polygonPoints.size()),
                () -> assertEquals(questionnaireQueryDTO.getPolygonPoints().get(0).getLat(),
                        polygonPoints.get(0).getLat()),
                () -> assertEquals(questionnaireQueryDTO.getPolygonPoints().get(0).getLon(),
                        polygonPoints.get(0).getLon())
        );
    }

    @Test
    void mapQuestionnaireQueryDTOToQuestionnaireQuery_AnswerQuery() {
        QuestionnaireQueryDTO questionnaireQueryDTO = new QuestionnaireQueryDTO();
        questionnaireQueryDTO.setAnswerQuery(
                createAnswerQuery());
        QuestionnaireQueryMapper questionnaireQueryMapper = new QuestionnaireQueryMapper();
        QuestionnaireQuery questionnaireQuery = questionnaireQueryMapper
                .mapQuestionnaireQueryDTOToQuestionnaireQuery(questionnaireQueryDTO);
        assertNotNull(questionnaireQuery);
        assertNotNull(questionnaireQuery.getAnswerQuery());
        final QuestionnaireQuery.AnswerQuery answerQuery = questionnaireQuery.getAnswerQuery();
        assertTrue(answerQuery.getOperation() instanceof QuestionnaireQuery.AndOperation);
        List<QuestionnaireQuery.Operation> subOperations = ((QuestionnaireQuery.AndOperation) answerQuery
                .getOperation()).getSubOperations();
        assertEquals(2, subOperations.size());
        assertTrue(subOperations.get(0) instanceof QuestionnaireQuery.AnswerOperation);
        QuestionnaireQuery.AnswerOperation answer = (QuestionnaireQuery.AnswerOperation) subOperations.get(0);
        assertEquals(QUESTION_ID, answer.getQuestionId());
        assertEquals(TEST_ANSWER, answer.getAnswer());
        assertTrue(subOperations.get(1) instanceof QuestionnaireQuery.OrOperation);
        QuestionnaireQuery.OrOperation or = (QuestionnaireQuery.OrOperation) subOperations.get(1);
        assertNotNull(or.getSubOperations());
        assertEquals(1, or.getSubOperations().size());
        assertEquals(DEFAULT_MIN_MATCH, or.getMinMatch());
    }

    @Test
    void mapQuestionnaireQueryDTOToQuestionnaireQuery_InvalidAnswerQueryOperation() {
        QuestionnaireQueryDTO questionnaireQueryDTO = new QuestionnaireQueryDTO();
        QuestionnaireQueryDTO.AnswerQueryDTO answerQueryDTO = new QuestionnaireQueryDTO.AnswerQueryDTO();
        answerQueryDTO.setOperation(new InvalidOperation());
        questionnaireQueryDTO.setAnswerQuery(answerQueryDTO);
        QuestionnaireQueryMapper questionnaireQueryMapper = new QuestionnaireQueryMapper();
        assertThrows(QueryParseException.class, () ->
                questionnaireQueryMapper.mapQuestionnaireQueryDTOToQuestionnaireQuery(questionnaireQueryDTO));
    }

    private QuestionnaireQueryDTO.AnswerQueryDTO createAnswerQuery() {
        QuestionnaireQueryDTO.AndOperationDTO and = new QuestionnaireQueryDTO.AndOperationDTO();
        QuestionnaireQueryDTO.AnswerDTO answer = new QuestionnaireQueryDTO.AnswerDTO(QUESTION_ID, TEST_ANSWER);
        and.getSubOperations().add(answer);
        QuestionnaireQueryDTO.OrOperationDTO or = new QuestionnaireQueryDTO.OrOperationDTO();
        or.setMinMatch(DEFAULT_MIN_MATCH);
        or.getSubOperations().add(answer);
        and.getSubOperations().add(or);
        return new QuestionnaireQueryDTO.AnswerQueryDTO(and);
    }

    private List<QuestionnaireQueryDTO.PointDTO> createPolygonPoints() {
        return IntStream.range(0, POINTS_COUNT).mapToObj(i -> new QuestionnaireQueryDTO.PointDTO(1.1 + i, 2.2 + i))
                .collect(Collectors.toList());
    }

    private static class InvalidOperation implements QuestionnaireQueryDTO.OperationDTO {

    }
}