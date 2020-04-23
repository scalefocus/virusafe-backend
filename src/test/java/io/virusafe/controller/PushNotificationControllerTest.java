package io.virusafe.controller;

import io.virusafe.mapper.QuestionnaireQueryMapper;
import io.virusafe.service.notification.PushNotificationSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PushNotificationControllerTest {

    private static final String PUSH_NOTIFICATION_REQUEST_DTO = "classpath:queries/pushNotificationRequestDTO.json";
    private static final String CUSTOM_NOTIFICATION_DTO = "classpath:json/customPushNotificationDTO.json";
    private static final String BASE_URL = "/admin/pushNotification";

    private MockMvc mockMvc;

    @Mock
    private PushNotificationSenderService pushNotificationService;

    @Mock
    private QuestionnaireQueryMapper questionnaireQueryMapper;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PushNotificationController(pushNotificationService, questionnaireQueryMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    public void sendPushNotificationTest() throws Exception {

        final String content = getResource(PUSH_NOTIFICATION_REQUEST_DTO);

        this.mockMvc.perform(post(BASE_URL + "/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void sendPushNotificationsToConcreteUsersTest() throws Exception {
        final String content = getResource(CUSTOM_NOTIFICATION_DTO);

        this.mockMvc.perform(post(BASE_URL + "/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String getResource(String path) throws IOException {
        final File inputFile = ResourceUtils.getFile(path);

        return Files.lines(inputFile.toPath()).collect(Collectors.joining());
    }
}
