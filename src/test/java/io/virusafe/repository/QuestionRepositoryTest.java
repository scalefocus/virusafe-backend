package io.virusafe.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.QuestionType;
import io.virusafe.domain.dto.QuestionDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestionRepositoryTest {

    private static final String DEFAULT_QUESTIONS_LANGUAGE = "bg";
    private static final String DEFAULT_QUESTIONS_FILE = "test_questions.json";
    private static final String TEST_QUESTIONS_LANGUAGE = "en";
    private static final int EXPECTED_QUESTIONS_SIZE = 2;
    private static final QuestionType TEST_QUESTION_TYPE = QuestionType.BOOLEAN;
    private static final String TEST_QUESTION_TYPE_STRING = "Bool";
    private static final String QUESTION_PREFIX = "Question";
    private static final int FIRST_QUESTION_ID = 1;
    private static final int SECOND_QUESTION_ID = 2;
    private static final String UNSUPPORTED_LANGUAGE = "de";
    private static final String INVALID_QUESTIONS_FILE = "invalid_questions.json";
    private static final int QUESTION_ID = 1;
    private static final int INVALID_QUESTION_ID = 100;
    private static final String MISSING_QUESTIONS_FILE = "missing_questions.json";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllQuestionsByLanguage() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, DEFAULT_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        List<QuestionDTO> questions = questionRepository.getAllQuestions(TEST_QUESTIONS_LANGUAGE);
        assertNotNull(questions);
        assertAll(
                () -> assertEquals(EXPECTED_QUESTIONS_SIZE, questions.size()),
                () -> validateQuestionDocument(questions, FIRST_QUESTION_ID, TEST_QUESTIONS_LANGUAGE),
                () -> validateQuestionDocument(questions, SECOND_QUESTION_ID, DEFAULT_QUESTIONS_LANGUAGE)
        );
    }

    @Test
    void getAllQuestionsWithoutLanguage() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, DEFAULT_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        List<QuestionDTO> questions = questionRepository.getAllQuestions(null);
        assertNotNull(questions);
        assertAll(
                () -> assertEquals(EXPECTED_QUESTIONS_SIZE, questions.size()),
                () -> validateQuestionDocument(questions, FIRST_QUESTION_ID, DEFAULT_QUESTIONS_LANGUAGE),
                () -> validateQuestionDocument(questions, SECOND_QUESTION_ID, DEFAULT_QUESTIONS_LANGUAGE)
        );
    }

    @Test
    void getAllQuestionsUnsupportedLanguage() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, DEFAULT_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        List<QuestionDTO> questions = questionRepository.getAllQuestions(UNSUPPORTED_LANGUAGE);
        assertNotNull(questions);
        assertAll(
                () -> assertEquals(EXPECTED_QUESTIONS_SIZE, questions.size()),
                () -> validateQuestionDocument(questions, FIRST_QUESTION_ID, DEFAULT_QUESTIONS_LANGUAGE),
                () -> validateQuestionDocument(questions, SECOND_QUESTION_ID, DEFAULT_QUESTIONS_LANGUAGE)
        );
    }

    @Test
    void getAllQuestionsInvalidQuestions() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, INVALID_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        List<QuestionDTO> questions = questionRepository.getAllQuestions(UNSUPPORTED_LANGUAGE);
        assertAll(
                () -> assertNotNull(questions),
                () -> assertEquals(0, questions.size())
        );
    }

    @Test
    void getAllQuestionsMissingFile() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, MISSING_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        List<QuestionDTO> questions = questionRepository.getAllQuestions(UNSUPPORTED_LANGUAGE);
        assertAll(
                () -> assertNotNull(questions),
                () -> assertEquals(0, questions.size())
        );
    }

    @Test
    void testFindById() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, DEFAULT_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        QuestionDTO question = questionRepository.findById(QUESTION_ID);
        assertAll(
                () -> assertNotNull(question),
                () -> assertEquals(QUESTION_ID, question.getId()),
                () -> assertEquals(QUESTION_PREFIX + QUESTION_ID + "_" + DEFAULT_QUESTIONS_LANGUAGE,
                        question.getQuestionTitle()),
                () -> assertEquals(TEST_QUESTION_TYPE, question.getQuestionType())
        );
    }

    @Test
    void testFindByMissingIdThrowsNoSuchElementException() {
        QuestionRepository questionRepository = new QuestionRepository(objectMapper, DEFAULT_QUESTIONS_FILE,
                DEFAULT_QUESTIONS_LANGUAGE);
        assertThrows(NoSuchElementException.class, () ->
                questionRepository.findById(INVALID_QUESTION_ID));
    }

    private void validateQuestionDocument(final List<QuestionDTO> questions, final int questionId,
                                          final String questionLanguage) {
        final QuestionDTO questionDTO = questions.get(questionId - 1);
        assertAll(
                () -> assertEquals(questionId, questionDTO.getId()),
                () -> assertEquals(TEST_QUESTION_TYPE, questionDTO.getQuestionType()),
                () -> assertEquals(TEST_QUESTION_TYPE_STRING, questionDTO.getQuestionType().getType()),
                () -> assertEquals(QUESTION_PREFIX + questionId + "_" + questionLanguage,
                        questionDTO.getQuestionTitle())
        );
    }
}