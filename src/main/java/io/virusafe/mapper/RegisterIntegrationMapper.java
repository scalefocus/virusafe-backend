package io.virusafe.mapper;

import io.virusafe.domain.Gender;
import io.virusafe.domain.document.QuestionnaireAnswer;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.RegisterIntegrationDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.service.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RegisterIntegrationMapper {

    private static final String TRUE = "true";
    private final UserDetailsService userDetailsService;

    /**
     * Construct a new RegisterIntegrationMapper, using the autowired UserDetailsService.
     *
     * @param userDetailsService the UserDetailsService to use
     */
    @Autowired
    public RegisterIntegrationMapper(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Map a QuestionnaireDocument to a RegisterIntrationDTO, using the passed user GUID to fetch UserDetails data.
     *
     * @param questionnaireDocument the QuestionnaireDocument to map
     * @param userGuid              the user GUID to use
     * @return the mapped RegisterIntegrationDTO
     */
    public RegisterIntegrationDTO mapQuestionnaireDTOToQuestionnaire(final QuestionnaireDocument questionnaireDocument,
                                                                     final String userGuid) {
        Optional<UserDetails> userDetailsOpt = userDetailsService.findByUserGuid(userGuid);
        if (userDetailsOpt.isEmpty()) {
            return null;
        }
        UserDetails userDetails = userDetailsOpt.get();
        return RegisterIntegrationDTO.builder()
                .identificationNumber(userDetails.getIdentificationNumberPlain())
                .identificationType(userDetails.getIdentificationType())
                .location(createLocation(questionnaireDocument.getGeoPoint()))
                .phone(userDetails.getPhoneNumber())
                .age(userDetails.getAge())
                .gender(convertGender(userDetails.getGender()))
                .preExistingConditions(userDetails.getPreExistingConditions())
                .timestamp(questionnaireDocument.getTimestamp())
                .serverTime(questionnaireDocument.getServerTime())
                .questions(createQuestions(questionnaireDocument.getAnswers()))
                .build();

    }

    private List<RegisterIntegrationDTO.RegisterAnswer> createQuestions(final List<QuestionnaireAnswer> answers) {
        return answers.stream().map(this::createRegisterAnswer).collect(Collectors.toList());
    }

    private RegisterIntegrationDTO.RegisterAnswer createRegisterAnswer(final QuestionnaireAnswer answer) {
        return RegisterIntegrationDTO.RegisterAnswer.builder().mobileId(answer.getQuestionId())
                .question(answer.getQuestionTitle()).answer(convertAnswerToBoolean(answer.getAnswer())).build();
    }

    private Boolean convertAnswerToBoolean(final String answer) {
        return TRUE.equalsIgnoreCase(answer);
    }

    private String convertGender(final Gender gender) {
        return gender == null ? null : gender.name();
    }

    private RegisterIntegrationDTO.RegisterLocation createLocation(final GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }
        return RegisterIntegrationDTO.RegisterLocation.builder().lat(geoPoint.getLat()).lon(geoPoint.getLon()).build();
    }


}
