package io.virusafe.mapper;

import io.virusafe.domain.QuestionType;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.AnswerDTO;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.QuestionDTO;
import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireMapperTest {

    private static final long CURRENT_TIME = System.currentTimeMillis();
    private static final double LAT = 23.74;
    private static final double LNG = 73.56;
    private static final String FIRST_QUESTION_TITLE = "Температура над 38C";
    private static final String SECOND_QUESTION_TITLE = "Суха кашлица";
    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";
    private static final int FIRST_QUESTION_ID = 1;
    private static final int SECOND_QUESTION_ID = 2;
    private static final int EXPECTED_DOCUMENTS_SIZE = 2;
    private static final String USER_GUID = "userGuid";

    private QuestionnaireMapper questionnaireMapper;

    @Mock
    private QuestionRepository questionRepository;

    private Clock clock = Clock.fixed(Instant.parse("2020-09-05T00:00:00.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setUp() {
        questionnaireMapper = new QuestionnaireMapper(questionRepository, clock);
    }

    @Test
    public void mapQuestionnaireDTOToQuestionnaireDocumentTest() {
        QuestionnairePostDTO questionnairePostDTO = createQuestionnairePostDTO();

        QuestionDTO firstQuestionDTO = createQuestionDTO(FIRST_QUESTION_ID, FIRST_QUESTION_TITLE, QuestionType.BOOLEAN);
        QuestionDTO secondQuestionDTO = createQuestionDTO(SECOND_QUESTION_ID, SECOND_QUESTION_TITLE,
                QuestionType.BOOLEAN);

        when(questionRepository.findById(FIRST_QUESTION_ID)).thenReturn(firstQuestionDTO);
        when(questionRepository.findById(SECOND_QUESTION_ID)).thenReturn(secondQuestionDTO);

        QuestionnaireDocument actualQuestionnaireDocument = questionnaireMapper
                .mapQuestionnaireDTOToQuestionnaire(questionnairePostDTO, USER_GUID);

        assertNotNull(actualQuestionnaireDocument);
        assertAll(
                () -> assertNotNull(actualQuestionnaireDocument.getGeoPoint()),
                () -> assertEquals(USER_GUID, actualQuestionnaireDocument.getUserGuid()),
                () -> assertEquals(LAT, actualQuestionnaireDocument.getGeoPoint().getLat(), 0.0),
                () -> assertEquals(LNG, actualQuestionnaireDocument.getGeoPoint().getLon(), 0.0),
                () -> assertEquals(CURRENT_TIME, actualQuestionnaireDocument.getTimestamp()),
                () -> assertEquals(clock.millis(), actualQuestionnaireDocument.getServerTime()),
                () -> assertNotNull(actualQuestionnaireDocument.getAnswers()),
                () -> assertEquals(EXPECTED_DOCUMENTS_SIZE, actualQuestionnaireDocument.getAnswers().size()),
                () -> assertEquals(FALSE, actualQuestionnaireDocument.getAnswers().get(0).getAnswer()),
                () -> assertEquals(FIRST_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(0).getQuestionId()),
                () -> assertEquals(FIRST_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(0).getQuestionTitle()),
                () -> assertEquals(TRUE, actualQuestionnaireDocument.getAnswers().get(1).getAnswer()),
                () -> assertEquals(SECOND_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(1).getQuestionId()),
                () -> assertEquals(SECOND_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(1).getQuestionTitle())
        );
    }

    @Test
    public void mapQuestionnaireDTOWithoutLocationToQuestionnaireDocumentTest() {
        QuestionnairePostDTO questionnairePostDTO = createQuestionnairePostDTO();
        questionnairePostDTO.setLocation(null);
        QuestionDTO firstQuestionDTO = createQuestionDTO(FIRST_QUESTION_ID, FIRST_QUESTION_TITLE, QuestionType.BOOLEAN);
        QuestionDTO secondQuestionDTO = createQuestionDTO(SECOND_QUESTION_ID, SECOND_QUESTION_TITLE,
                QuestionType.BOOLEAN);

        when(questionRepository.findById(FIRST_QUESTION_ID)).thenReturn(firstQuestionDTO);
        when(questionRepository.findById(SECOND_QUESTION_ID)).thenReturn(secondQuestionDTO);

        QuestionnaireDocument actualQuestionnaireDocument = questionnaireMapper
                .mapQuestionnaireDTOToQuestionnaire(questionnairePostDTO, USER_GUID);

        assertNotNull(actualQuestionnaireDocument);
        assertAll(
                () -> assertEquals(USER_GUID, actualQuestionnaireDocument.getUserGuid()),
                () -> assertNull(actualQuestionnaireDocument.getGeoPoint()),
                () -> assertEquals(CURRENT_TIME, actualQuestionnaireDocument.getTimestamp()),
                () -> assertEquals(clock.millis(), actualQuestionnaireDocument.getServerTime()),
                () -> assertNotNull(actualQuestionnaireDocument.getAnswers()),
                () -> assertEquals(EXPECTED_DOCUMENTS_SIZE, actualQuestionnaireDocument.getAnswers().size()),
                () -> assertEquals(FALSE, actualQuestionnaireDocument.getAnswers().get(0).getAnswer()),
                () -> assertEquals(FIRST_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(0).getQuestionId()),
                () -> assertEquals(FIRST_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(0).getQuestionTitle()),
                () -> assertEquals(TRUE, actualQuestionnaireDocument.getAnswers().get(1).getAnswer()),
                () -> assertEquals(SECOND_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(1).getQuestionId()),
                () -> assertEquals(SECOND_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(1).getQuestionTitle())
        );
    }

    @Test
    public void mapQuestionnaireDTOWithoutLocationLatToQuestionnaireDocumentTest() {
        QuestionnairePostDTO questionnairePostDTO = createQuestionnairePostDTO();
        questionnairePostDTO.getLocation().setLat(null);
        QuestionDTO firstQuestionDTO = createQuestionDTO(FIRST_QUESTION_ID, FIRST_QUESTION_TITLE, QuestionType.BOOLEAN);
        QuestionDTO secondQuestionDTO = createQuestionDTO(SECOND_QUESTION_ID, SECOND_QUESTION_TITLE,
                QuestionType.BOOLEAN);

        when(questionRepository.findById(FIRST_QUESTION_ID)).thenReturn(firstQuestionDTO);
        when(questionRepository.findById(SECOND_QUESTION_ID)).thenReturn(secondQuestionDTO);

        QuestionnaireDocument actualQuestionnaireDocument = questionnaireMapper
                .mapQuestionnaireDTOToQuestionnaire(questionnairePostDTO, USER_GUID);

        assertNotNull(actualQuestionnaireDocument);
        assertAll(
                () -> assertEquals(USER_GUID, actualQuestionnaireDocument.getUserGuid()),
                () -> assertNull(actualQuestionnaireDocument.getGeoPoint()),
                () -> assertEquals(CURRENT_TIME, actualQuestionnaireDocument.getTimestamp()),
                () -> assertEquals(clock.millis(), actualQuestionnaireDocument.getServerTime()),
                () -> assertNotNull(actualQuestionnaireDocument.getAnswers()),
                () -> assertEquals(EXPECTED_DOCUMENTS_SIZE, actualQuestionnaireDocument.getAnswers().size()),
                () -> assertEquals(FALSE, actualQuestionnaireDocument.getAnswers().get(0).getAnswer()),
                () -> assertEquals(FIRST_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(0).getQuestionId()),
                () -> assertEquals(FIRST_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(0).getQuestionTitle()),
                () -> assertEquals(TRUE, actualQuestionnaireDocument.getAnswers().get(1).getAnswer()),
                () -> assertEquals(SECOND_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(1).getQuestionId()),
                () -> assertEquals(SECOND_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(1).getQuestionTitle())
        );
    }

    @Test
    public void mapQuestionnaireDTOWithoutLocationLngToQuestionnaireDocumentTest() {
        QuestionnairePostDTO questionnairePostDTO = createQuestionnairePostDTO();
        questionnairePostDTO.getLocation().setLng(null);
        QuestionDTO firstQuestionDTO = createQuestionDTO(FIRST_QUESTION_ID, FIRST_QUESTION_TITLE, QuestionType.BOOLEAN);
        QuestionDTO secondQuestionDTO = createQuestionDTO(SECOND_QUESTION_ID, SECOND_QUESTION_TITLE,
                QuestionType.BOOLEAN);

        when(questionRepository.findById(FIRST_QUESTION_ID)).thenReturn(firstQuestionDTO);
        when(questionRepository.findById(SECOND_QUESTION_ID)).thenReturn(secondQuestionDTO);

        QuestionnaireDocument actualQuestionnaireDocument = questionnaireMapper
                .mapQuestionnaireDTOToQuestionnaire(questionnairePostDTO, USER_GUID);

        assertNotNull(actualQuestionnaireDocument);
        assertAll(
                () -> assertEquals(USER_GUID, actualQuestionnaireDocument.getUserGuid()),
                () -> assertNull(actualQuestionnaireDocument.getGeoPoint()),
                () -> assertEquals(CURRENT_TIME, actualQuestionnaireDocument.getTimestamp()),
                () -> assertEquals(clock.millis(), actualQuestionnaireDocument.getServerTime()),
                () -> assertNotNull(actualQuestionnaireDocument.getAnswers()),
                () -> assertEquals(EXPECTED_DOCUMENTS_SIZE, actualQuestionnaireDocument.getAnswers().size()),
                () -> assertEquals(FALSE, actualQuestionnaireDocument.getAnswers().get(0).getAnswer()),
                () -> assertEquals(FIRST_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(0).getQuestionId()),
                () -> assertEquals(FIRST_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(0).getQuestionTitle()),
                () -> assertEquals(TRUE, actualQuestionnaireDocument.getAnswers().get(1).getAnswer()),
                () -> assertEquals(SECOND_QUESTION_ID, actualQuestionnaireDocument.getAnswers().get(1).getQuestionId()),
                () -> assertEquals(SECOND_QUESTION_TITLE,
                        actualQuestionnaireDocument.getAnswers().get(1).getQuestionTitle())
        );
    }

    private QuestionnairePostDTO createQuestionnairePostDTO() {
        QuestionnairePostDTO questionnairePostDTO = new QuestionnairePostDTO();
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setAnswer(FALSE);
        firstAnswerDTO.setQuestionId(FIRST_QUESTION_ID);

        AnswerDTO secondAnswerDTO = new AnswerDTO();
        secondAnswerDTO.setAnswer(TRUE);
        secondAnswerDTO.setQuestionId(SECOND_QUESTION_ID);

        List<AnswerDTO> answerDTOS = Arrays.asList(firstAnswerDTO, secondAnswerDTO);
        questionnairePostDTO.setAnswers(answerDTOS);

        Location location = new Location();
        location.setLat(LAT);
        location.setLng(LNG);
        questionnairePostDTO.setLocation(location);
        questionnairePostDTO.setTimestamp(CURRENT_TIME);

        return questionnairePostDTO;
    }

    private QuestionDTO createQuestionDTO(Integer id, String questionTitle, QuestionType questionType) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(id);
        questionDTO.setQuestionTitle(questionTitle);
        questionDTO.setQuestionType(questionType);

        return questionDTO;
    }
}
