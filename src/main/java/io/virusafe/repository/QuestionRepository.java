package io.virusafe.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virusafe.domain.QuestionType;
import io.virusafe.domain.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuestionRepository {

    private final String questionsFilePath;

    private final ObjectMapper objectMapper;

    private final String defaultLanguage;

    /**
     * Construct a new QuestionRepository, using the autowired ObjectMapper and the configured file path and
     * default language.
     *
     * @param objectMapper      the ObjectMapper to use for deserialization
     * @param questionsFilePath the path to the questions JSON
     * @param defaultLanguage   the default language to use when no language has been provided
     */
    public QuestionRepository(final ObjectMapper objectMapper,
                              @Value("${questions.file.path}") final String questionsFilePath,
                              @Value("${questions.default.language}") final String defaultLanguage) {
        this.objectMapper = objectMapper;
        this.questionsFilePath = questionsFilePath;
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Fetch all questions in a given language from the question JSON file.
     * This method caches responses for performance.
     *
     * @param language the language to fetch questions for
     * @return the list of QuestionDTOs
     */
    @Cacheable("allQuestions")
    public List<QuestionDTO> getAllQuestions(final String language) {
        try (InputStream questionsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(questionsFilePath)) {
            if (questionsStream == null) {
                log.error("Error occurred while trying to read questions from file: {}.", questionsFilePath);
                return Collections.emptyList();
            }
            return Arrays.stream(objectMapper.readValue(questionsStream, PersistedQuestions[].class))
                    .filter(PersistedQuestions::getEnabled).map(question -> createQuestion(question, language))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error occurred while trying to read questions from file: {}. Error message: {}",
                    questionsFilePath, e.getMessage());
            return Collections.emptyList();
        }
    }

    private QuestionDTO createQuestion(final PersistedQuestions question, final String language) {
        return new QuestionDTO(question.getId(), getQuestionTitle(language, question.getQuestionTitles()),
                question.getQuestionType());
    }

    private String getQuestionTitle(final String language, final Map<String, String> questionTitles) {
        if (language == null || !questionTitles.containsKey(language)) {
            return questionTitles.get(defaultLanguage);
        }
        return questionTitles.get(language);
    }

    /**
     * Fetch a specific question by its ID, in the configured default language.
     * This method caches responses for performance.
     *
     * @param questionId the ID of the question to fetch
     * @return the QuestionDTO corresponding to this ID
     */
    @Cacheable("questionById")
    public QuestionDTO findById(final Integer questionId) {
        return getAllQuestions(defaultLanguage).stream().filter(que -> que.getId().equals(questionId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No question with this id"));
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    private static final class PersistedQuestions {

        private Integer id;

        private Map<String, String> questionTitles;

        private QuestionType questionType;

        private Boolean enabled = Boolean.TRUE;

    }
}
