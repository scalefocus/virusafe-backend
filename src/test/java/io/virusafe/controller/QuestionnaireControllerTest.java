package io.virusafe.controller;

import io.virusafe.controller.configuration.AuthenticationPrincipalResolver;
import io.virusafe.domain.QuestionType;
import io.virusafe.domain.dto.AnswerDTO;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.QuestionDTO;
import io.virusafe.domain.dto.QuestionnairePostDTO;
import io.virusafe.exception.handler.GlobalExceptionHandler;
import io.virusafe.service.questionnaire.QuestionnaireService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireControllerTest {

    private static final String BASE_URL = "/questionnaire";
    private static final String USER_GUID = "USER_GUID";
    private static final String ALL_QUESTIONS = "classpath:json/allQuestions.json";
    private static final String ALL_QUESTIONNAIRE_POST_DTO = "classpath:json/allQuestionnairePostDTO.json";
    private static final String IDENTIFICATION_NUMBER = "0000000000";

    private MockMvc mockMvc;

    @Mock
    private QuestionnaireService questionnaireService;

    @Mock
    private Validator validator;

    @Test
    public void getAllQuestions() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QuestionnaireController(questionnaireService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, IDENTIFICATION_NUMBER))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        final String responseBody = getResource(ALL_QUESTIONS);

        when(questionnaireService.getQuestionnaire(anyString())).thenReturn(createMockedQuestionDTOs());

        this.mockMvc.perform(get(BASE_URL).header("language", "bg"))
                .andExpect(content().json(responseBody))
                .andExpect(status().isOk());
    }

    @Test
    public void postQuestionnaire() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QuestionnaireController(questionnaireService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, IDENTIFICATION_NUMBER))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
        final String content = getResource(ALL_QUESTIONNAIRE_POST_DTO);

        doNothing().when(questionnaireService).postQuestionnaire(createMockedQuestionnairePostDTO(), USER_GUID);

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostQuestionnaireReturnsForbiddenWithInvalidUserInformation() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new QuestionnaireController(questionnaireService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, null))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
        final String content = getResource(ALL_QUESTIONNAIRE_POST_DTO);

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    private String getResource(String path) throws IOException {
        final File inputFile = ResourceUtils.getFile(path);

        return Files.lines(inputFile.toPath()).collect(Collectors.joining());
    }

    private List<QuestionDTO> createMockedQuestionDTOs() {
        QuestionDTO firstQuestionDTO = new QuestionDTO();
        firstQuestionDTO.setId(1);
        firstQuestionDTO.setQuestionTitle("Question1_bg");
        firstQuestionDTO.setQuestionType(QuestionType.BOOLEAN);
        QuestionDTO secondQuestionDTO = new QuestionDTO();
        secondQuestionDTO.setId(2);
        secondQuestionDTO.setQuestionTitle("Question2_bg");
        secondQuestionDTO.setQuestionType(QuestionType.BOOLEAN);
        QuestionDTO thirdQuestionDTO = new QuestionDTO();
        thirdQuestionDTO.setId(3);
        thirdQuestionDTO.setQuestionTitle("Question3_bg");
        thirdQuestionDTO.setQuestionType(QuestionType.BOOLEAN);

        return Arrays.asList(firstQuestionDTO, secondQuestionDTO, thirdQuestionDTO);
    }

    private QuestionnairePostDTO createMockedQuestionnairePostDTO() {
        QuestionnairePostDTO questionnairePostDTO = new QuestionnairePostDTO();
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswer("TRUE");
        answerDTO.setQuestionId(1);
        questionnairePostDTO.setAnswers(Collections.singletonList(answerDTO));
        Location location = new Location();
        location.setLat(55.75);
        location.setLng(43.21);
        questionnairePostDTO.setLocation(location);
        questionnairePostDTO.setTimestamp(111222333L);

        return questionnairePostDTO;
    }
}
