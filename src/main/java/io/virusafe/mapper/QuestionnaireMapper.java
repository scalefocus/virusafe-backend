package io.virusafe.mapper;

import io.virusafe.domain.document.QuestionnaireAnswer;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.AnswerDTO;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.repository.QuestionRepository;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionnaireMapper {

    private final QuestionRepository questionRepository;

    private final Clock clock;

    /**
     * Construct a new QuestionnaireMapper, using the autowired QuestionRespository and system Clock.
     *
     * @param questionRepository the QuestionRepository to use
     * @param clock              the Clock to use
     */
    public QuestionnaireMapper(final QuestionRepository questionRepository, final Clock clock) {
        this.questionRepository = questionRepository;
        this.clock = clock;
    }

    /**
     * Map a QuestionnairePostDTO to a QuestionnaireDocument, using the provided user GUID to fetch UserDetails data.
     *
     * @param questionnairePostDTO the QuestionnairePostDTO to map
     * @param userGuid             the user GUID to use
     * @return the mapped QuestionnaireDocument
     */
    public QuestionnaireDocument mapQuestionnaireDTOToQuestionnaire(final QuestionnairePostDTO questionnairePostDTO,
                                                                    final String userGuid) {
        final GeoPoint geoPoint = createGeoPoint(questionnairePostDTO.getLocation());

        return QuestionnaireDocument.builder()
                .answers(createAnswers(questionnairePostDTO.getAnswers()))
                .userGuid(userGuid)
                .geoPoint(geoPoint)
                .timestamp(questionnairePostDTO.getTimestamp())
                .serverTime(clock.millis()).build();
    }

    private List<QuestionnaireAnswer> createAnswers(final List<AnswerDTO> answers) {
        return answers.stream().map(answer -> createAnswerFromAnswerDTO(answer)).collect(Collectors.toList());
    }

    private QuestionnaireAnswer createAnswerFromAnswerDTO(final AnswerDTO answerDTO) {
        return QuestionnaireAnswer.builder().questionId(answerDTO.getQuestionId())
                .questionTitle(questionRepository.findById(answerDTO.getQuestionId()).getQuestionTitle())
                .answer(answerDTO.getAnswer()).build();
    }

    private GeoPoint createGeoPoint(final Location location) {
        if (hasSetLocation(location)) {
            return new GeoPoint(location.getLat(), location.getLng());
        }
        return null;
    }

    private boolean hasSetLocation(final Location location) {
        return location != null && location.getLat() != null &&
                location.getLng() != null;
    }

}
