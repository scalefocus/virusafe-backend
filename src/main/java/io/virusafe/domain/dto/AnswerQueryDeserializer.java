package io.virusafe.domain.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.virusafe.exception.QueryParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class AnswerQueryDeserializer extends StdDeserializer<QuestionnaireQueryDTO.AnswerQueryDTO> {

    private static final String AND = "/and";
    private static final String OR = "/or";
    private static final String EQ = "/eq";
    private static final String QUESTION_QUESTION_ID = "questionId";
    private static final String QUESTION_ANSWER = "answer";
    private static final int SINGLE_ELEMENT = 1;
    private static final String MIN_MATCH = "/minMatch";

    /**
     * Construct an AnswerQueryDeserializer with no value class.
     */
    public AnswerQueryDeserializer() {
        this(null);
    }

    /**
     * Construct an AnswerQueryDeserializer with the passed value class.
     *
     * @param vc the value class
     */
    public AnswerQueryDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public QuestionnaireQueryDTO.AnswerQueryDTO deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<QuestionnaireQueryDTO.OperationDTO> operations = traverseTree(node);
        if (operations.size() != SINGLE_ELEMENT) {
            throw new QueryParseException("More than one root nodes in 'answerQuery'");
        }
        return new QuestionnaireQueryDTO.AnswerQueryDTO(operations.get(0));
    }

    private List<QuestionnaireQueryDTO.OperationDTO> traverseTree(final JsonNode node) {
        List<QuestionnaireQueryDTO.OperationDTO> operations = new ArrayList<>();
        JsonNode andNode = node.at(AND);
        if (!andNode.isEmpty()) {
            operations.add(parseAnd(andNode));
        }
        JsonNode orNode = node.at(OR);
        if (!orNode.isEmpty()) {
            operations.add(parseOr(orNode));
        }
        JsonNode eqNode = node.at(EQ);
        if (!eqNode.isEmpty()) {
            operations.add(parseQuestionEq(eqNode));
        }
        return operations;
    }

    private QuestionnaireQueryDTO.OperationDTO parseAnd(final JsonNode andNode) {
        return new QuestionnaireQueryDTO.AndOperationDTO(parseCompositeOperation(andNode, AND));
    }

    private List<QuestionnaireQueryDTO.OperationDTO> parseCompositeOperation(final JsonNode andNode,
                                                                             final String name) {
        if (!andNode.isArray()) {
            throw new QueryParseException(name + " should be array");
        }
        List<QuestionnaireQueryDTO.OperationDTO> operations = new ArrayList<>();
        for (JsonNode subNode : andNode) {
            operations.addAll(traverseTree(subNode));
        }
        return operations;
    }

    private QuestionnaireQueryDTO.OperationDTO parseOr(final JsonNode orNode) {
        String minMatch = parseOrMinMatch(orNode);
        return new QuestionnaireQueryDTO.OrOperationDTO(parseCompositeOperation(orNode, OR), minMatch);
    }

    private QuestionnaireQueryDTO.OperationDTO parseQuestionEq(final JsonNode andNode) {
        JsonNode questionIdNode = andNode.get(QUESTION_QUESTION_ID);
        JsonNode answerNode = andNode.get(QUESTION_ANSWER);
        if (Objects.nonNull(questionIdNode) && Objects.nonNull(answerNode)) {
            String questionId = questionIdNode.asText();
            String answer = answerNode.asText();
            return new QuestionnaireQueryDTO.AnswerDTO(questionId, answer);
        } else {
            throw new QueryParseException(
                    QUESTION_QUESTION_ID + " and " + QUESTION_ANSWER + " are mandatory in answer");
        }
    }

    private String parseOrMinMatch(final JsonNode orNode) {
        return StreamSupport.stream(orNode.spliterator(), false)
                .map(subNode -> subNode.at(MIN_MATCH))
                .filter(JsonNode::isValueNode)
                .map(JsonNode::asText)
                .findFirst()
                .orElse(null);
    }
}