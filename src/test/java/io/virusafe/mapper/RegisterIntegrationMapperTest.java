package io.virusafe.mapper;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.document.QuestionnaireAnswer;
import io.virusafe.domain.document.QuestionnaireDocument;
import io.virusafe.domain.dto.RegisterIntegrationDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterIntegrationMapperTest {
    public static final String USER_GUID = "USER_GUID";
    public static final double LONGITUDE = 1.4;
    public static final double LATITUDE = 2.3;
    public static final String ID = "ID";
    public static final long TIMESTAMP = 105L;
    public static final long SERVER_TIME = 111L;
    public static final String ANSWER = "true";
    public static final int QUESTION_ID = 1;
    public static final String TITLE = "TITLE";
    public static final int AGE = 29;
    public static final String PASSPORT = "PASSPORT";
    public static final String CONDITIONS = "CONDITIONS";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    @Mock
    private UserDetailsService userDetailsService;

    private RegisterIntegrationMapper registerIntegrationMapper;

    @BeforeEach
    public void setUp() {
        registerIntegrationMapper = new RegisterIntegrationMapper(userDetailsService);
    }

    @Test
    public void testMapQuestionnaireDTOToQuestionnaire() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .age(AGE)
                .gender(Gender.FEMALE)
                .identificationType(IdentificationType.PASSPORT)
                .identificationNumberPlain(PASSPORT)
                .preExistingConditions(CONDITIONS)
                .phoneNumber(PHONE_NUMBER)
                .build();
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
                .userGuid(USER_GUID)
                .serverTime(SERVER_TIME)
                .timestamp(TIMESTAMP)
                .id(ID)
                .geoPoint(new GeoPoint(LATITUDE, LONGITUDE))
                .answers(Collections.singletonList(
                        QuestionnaireAnswer.builder()
                                .answer(ANSWER)
                                .questionId(QUESTION_ID)
                                .questionTitle(TITLE)
                                .build()
                ))
                .build();
        RegisterIntegrationDTO registerIntegrationDTO =
                registerIntegrationMapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, USER_GUID);

        assertAll(
                () -> assertNotNull(registerIntegrationDTO),
                () -> assertEquals(AGE, registerIntegrationDTO.getAge()),
                () -> assertEquals(Gender.FEMALE.name(), registerIntegrationDTO.getGender()),
                () -> assertEquals(IdentificationType.PASSPORT, registerIntegrationDTO.getIdentificationType()),
                () -> assertEquals(PASSPORT, registerIntegrationDTO.getIdentificationNumber()),
                () -> assertEquals(CONDITIONS, registerIntegrationDTO.getPreExistingConditions()),
                () -> assertEquals(PHONE_NUMBER, registerIntegrationDTO.getPhone()),
                () -> assertEquals(LATITUDE, registerIntegrationDTO.getLocation().getLat()),
                () -> assertEquals(LONGITUDE, registerIntegrationDTO.getLocation().getLon()),
                () -> assertEquals(1, registerIntegrationDTO.getQuestions().size()),
                () -> assertTrue(registerIntegrationDTO.getQuestions().get(0).getAnswer()),
                () -> assertEquals(TITLE, registerIntegrationDTO.getQuestions().get(0).getQuestion()),
                () -> assertEquals(QUESTION_ID, registerIntegrationDTO.getQuestions().get(0).getMobileId()),
                () -> assertEquals(TIMESTAMP, registerIntegrationDTO.getTimestamp()),
                () -> assertEquals(SERVER_TIME, registerIntegrationDTO.getServerTime())
        );
    }

    @Test
    public void testMapQuestionnaireDTOToQuestionnaireMissingUser() {
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
                .userGuid(USER_GUID)
                .serverTime(SERVER_TIME)
                .timestamp(TIMESTAMP)
                .id(ID)
                .geoPoint(new GeoPoint(LATITUDE, LONGITUDE))
                .answers(Collections.singletonList(
                        QuestionnaireAnswer.builder()
                                .answer(ANSWER)
                                .questionId(QUESTION_ID)
                                .questionTitle(TITLE)
                                .build()
                ))
                .build();
        RegisterIntegrationDTO registerIntegrationDTO =
                registerIntegrationMapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, USER_GUID);
        assertNull(registerIntegrationDTO);
    }

    @Test
    public void testMapQuestionnaireDTOToQuestionnaireNullGender() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .age(AGE)
                .identificationType(IdentificationType.PASSPORT)
                .identificationNumberPlain(PASSPORT)
                .preExistingConditions(CONDITIONS)
                .phoneNumber(PHONE_NUMBER)
                .build();
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
                .userGuid(USER_GUID)
                .serverTime(SERVER_TIME)
                .timestamp(TIMESTAMP)
                .id(ID)
                .geoPoint(new GeoPoint(LATITUDE, LONGITUDE))
                .answers(Collections.singletonList(
                        QuestionnaireAnswer.builder()
                                .answer(ANSWER)
                                .questionId(QUESTION_ID)
                                .questionTitle(TITLE)
                                .build()
                ))
                .build();
        RegisterIntegrationDTO registerIntegrationDTO =
                registerIntegrationMapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, USER_GUID);

        assertAll(
                () -> assertNotNull(registerIntegrationDTO),
                () -> assertEquals(AGE, registerIntegrationDTO.getAge()),
                () -> assertNull(registerIntegrationDTO.getGender()),
                () -> assertEquals(IdentificationType.PASSPORT, registerIntegrationDTO.getIdentificationType()),
                () -> assertEquals(PASSPORT, registerIntegrationDTO.getIdentificationNumber()),
                () -> assertEquals(CONDITIONS, registerIntegrationDTO.getPreExistingConditions()),
                () -> assertEquals(PHONE_NUMBER, registerIntegrationDTO.getPhone()),
                () -> assertEquals(LATITUDE, registerIntegrationDTO.getLocation().getLat()),
                () -> assertEquals(LONGITUDE, registerIntegrationDTO.getLocation().getLon()),
                () -> assertEquals(1, registerIntegrationDTO.getQuestions().size()),
                () -> assertTrue(registerIntegrationDTO.getQuestions().get(0).getAnswer()),
                () -> assertEquals(TITLE, registerIntegrationDTO.getQuestions().get(0).getQuestion()),
                () -> assertEquals(QUESTION_ID, registerIntegrationDTO.getQuestions().get(0).getMobileId()),
                () -> assertEquals(TIMESTAMP, registerIntegrationDTO.getTimestamp()),
                () -> assertEquals(SERVER_TIME, registerIntegrationDTO.getServerTime())
        );
    }

    @Test
    public void testMapQuestionnaireDTOToQuestionnaireNullLocation() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .age(AGE)
                .gender(Gender.FEMALE)
                .identificationType(IdentificationType.PASSPORT)
                .identificationNumberPlain(PASSPORT)
                .preExistingConditions(CONDITIONS)
                .phoneNumber(PHONE_NUMBER)
                .build();
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        QuestionnaireDocument questionnaireDocument = QuestionnaireDocument.builder()
                .userGuid(USER_GUID)
                .serverTime(SERVER_TIME)
                .timestamp(TIMESTAMP)
                .id(ID)
                .answers(Collections.singletonList(
                        QuestionnaireAnswer.builder()
                                .answer(ANSWER)
                                .questionId(QUESTION_ID)
                                .questionTitle(TITLE)
                                .build()
                ))
                .build();
        RegisterIntegrationDTO registerIntegrationDTO =
                registerIntegrationMapper.mapQuestionnaireDTOToQuestionnaire(questionnaireDocument, USER_GUID);

        assertAll(
                () -> assertNotNull(registerIntegrationDTO),
                () -> assertEquals(AGE, registerIntegrationDTO.getAge()),
                () -> assertEquals(Gender.FEMALE.name(), registerIntegrationDTO.getGender()),
                () -> assertEquals(IdentificationType.PASSPORT, registerIntegrationDTO.getIdentificationType()),
                () -> assertEquals(PASSPORT, registerIntegrationDTO.getIdentificationNumber()),
                () -> assertEquals(CONDITIONS, registerIntegrationDTO.getPreExistingConditions()),
                () -> assertEquals(PHONE_NUMBER, registerIntegrationDTO.getPhone()),
                () -> assertNull(registerIntegrationDTO.getLocation()),
                () -> assertEquals(1, registerIntegrationDTO.getQuestions().size()),
                () -> assertTrue(registerIntegrationDTO.getQuestions().get(0).getAnswer()),
                () -> assertEquals(TITLE, registerIntegrationDTO.getQuestions().get(0).getQuestion()),
                () -> assertEquals(QUESTION_ID, registerIntegrationDTO.getQuestions().get(0).getMobileId()),
                () -> assertEquals(TIMESTAMP, registerIntegrationDTO.getTimestamp()),
                () -> assertEquals(SERVER_TIME, registerIntegrationDTO.getServerTime())
        );
    }
}