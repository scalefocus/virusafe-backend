package io.virusafe.mapper;

import io.virusafe.domain.dto.QuestionnaireQueryDTO;
import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.QueryParseException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionnaireQueryMapper {

    /**
     * Map a QuestionnaireQueryDTO to a QuestionnaireQuery.
     *
     * @param questionnaireQueryDTO the QuestionnaireQueryDTO to map
     * @return the mapped QuestionnaireQuery
     */
    public QuestionnaireQuery mapQuestionnaireQueryDTOToQuestionnaireQuery(
            final QuestionnaireQueryDTO questionnaireQueryDTO) {
        QuestionnaireQuery questionnaireQuery = new QuestionnaireQuery();
        if (questionnaireQueryDTO.getTimeSlot() != null) {
            questionnaireQuery.setTimeSlot(createTimeSlot(questionnaireQueryDTO.getTimeSlot()));
        }
        if (questionnaireQueryDTO.getPolygonPoints() != null) {
            questionnaireQuery.setPolygonPoints(createPolygon(questionnaireQueryDTO.getPolygonPoints()));
        }
        if (questionnaireQueryDTO.getAnswerQuery() != null) {
            questionnaireQuery.setAnswerQuery(createAnswerQuery(questionnaireQueryDTO.getAnswerQuery()));
        }
        return questionnaireQuery;
    }

    private QuestionnaireQuery.AnswerQuery createAnswerQuery(
            final QuestionnaireQueryDTO.AnswerQueryDTO answerQueryDTO) {
        return QuestionnaireQuery.AnswerQuery.builder().operation(createOperation(answerQueryDTO.getOperation()))
                .build();
    }

    private QuestionnaireQuery.Operation createOperation(final QuestionnaireQueryDTO.OperationDTO operationDTO) {
        if (operationDTO instanceof QuestionnaireQueryDTO.AndOperationDTO) {
            return QuestionnaireQuery.AndOperation.builder()
                    .subOperations(createComposable((QuestionnaireQueryDTO.ComposableOperationDTO) operationDTO))
                    .build();
        }
        if (operationDTO instanceof QuestionnaireQueryDTO.OrOperationDTO) {
            return QuestionnaireQuery.OrOperation.builder()
                    .subOperations(createComposable((QuestionnaireQueryDTO.ComposableOperationDTO) operationDTO))
                    .minMatch(((QuestionnaireQueryDTO.OrOperationDTO) operationDTO).getMinMatch())
                    .build();
        }
        if (operationDTO instanceof QuestionnaireQueryDTO.AnswerDTO) {
            QuestionnaireQueryDTO.AnswerDTO answerDTO = (QuestionnaireQueryDTO.AnswerDTO) operationDTO;
            return QuestionnaireQuery.AnswerOperation.builder().questionId(answerDTO.getQuestionId())
                    .answer(answerDTO.getAnswer()).build();
        }
        throw new QueryParseException("Cannot convert operation");
    }

    private List<QuestionnaireQuery.Operation> createComposable(
            final QuestionnaireQueryDTO.ComposableOperationDTO operationDTO) {
        return operationDTO.getSubOperations().stream().map(subOperation -> createOperation(subOperation)).collect(
                Collectors.toList());
    }

    private List<QuestionnaireQuery.Point> createPolygon(final List<QuestionnaireQueryDTO.PointDTO> polygonPointsDTO) {
        return polygonPointsDTO.stream().map(point -> new QuestionnaireQuery.Point(point.getLat(), point.getLon()))
                .collect(Collectors.toList());
    }

    private QuestionnaireQuery.TimeSlot createTimeSlot(final QuestionnaireQueryDTO.TimeSlotDTO timeSlotDTO) {
        return QuestionnaireQuery.TimeSlot.builder().gt(timeSlotDTO.getGt()).gte(timeSlotDTO.getGte())
                .lt(timeSlotDTO.getLt()).lte(timeSlotDTO.getLte()).build();
    }

}
