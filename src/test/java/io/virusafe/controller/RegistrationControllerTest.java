package io.virusafe.controller;

import io.virusafe.controller.configuration.AuthenticationPrincipalResolver;
import io.virusafe.domain.dto.AccessTokenDTO;
import io.virusafe.exception.handler.GlobalExceptionHandler;
import io.virusafe.service.pin.PinService;
import io.virusafe.service.token.TokenService;
import io.virusafe.service.userdetails.UserDetailsService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    private static final String USER_GUID = "USER_GUID";
    private static final String PIN_GENERATION_DTO = "classpath:json/pinGenerationDTO.json";
    private static final String TOKEN_GENERATION_DTO = "classpath:json/tokenGenerationDTO.json";
    private static final String ACCESS_TOKEN_DTO = "classpath:json/accessTokenDTO.json";
    private static final String PUSH_TOKEN_DTO = "classpath:json/pushTokenDTO.json";
    private static final String REFRESH_TOKEN_DTO = "classpath:json/refreshToken.json";
    private static final String IDENTIFICATION_NUMBER = "0000000000";
    public static final String PHONE_NUMBER = "0898200300";
    public static final String PIN = "000000";

    private MockMvc mockMvc;

    @Mock
    private TokenService tokenService;

    @Mock
    private PinService pinService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RegistrationController(tokenService, pinService, userDetailsService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, IDENTIFICATION_NUMBER))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    public void createPin() throws Exception {

        final String content = getResource(PIN_GENERATION_DTO);
        doNothing().when(pinService).generatePin(PHONE_NUMBER);

        this.mockMvc.perform(post("/pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createTokenSuccessfully() throws Exception {
        final String content = getResource(TOKEN_GENERATION_DTO);
        final String responseBody = getResource(ACCESS_TOKEN_DTO);

        when(pinService.verifyPin(PHONE_NUMBER, PIN)).thenReturn(true);
        AccessTokenDTO accessTokenDTO = AccessTokenDTO.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        when(tokenService.generateToken(PHONE_NUMBER)).thenReturn(accessTokenDTO);
        doNothing().when(pinService).invalidatePins(PHONE_NUMBER);

        this.mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseBody));
    }

    @Test
    public void testCreateTokenReturns438ForInvalidPin() throws Exception {

        final String content = getResource(TOKEN_GENERATION_DTO);
        final String responseBody = getResource(ACCESS_TOKEN_DTO);

        when(pinService.verifyPin(PHONE_NUMBER, PIN)).thenReturn(false);

        this.mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(438));
    }

    @Test
    public void addPushToken() throws Exception {
        final String content = getResource(PUSH_TOKEN_DTO);

        doNothing().when(userDetailsService).updatePushToken(USER_GUID, "pushToken");

        this.mockMvc.perform(post("/pushtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createRefreshToken() throws Exception {
        final String content = getResource(REFRESH_TOKEN_DTO);
        final String responseBody = getResource(ACCESS_TOKEN_DTO);

        when(tokenService.refreshToken("refreshToken")).thenReturn(createMockedAccessTokenDTO());
        this.mockMvc.perform(post("/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(responseBody));
        ;
    }

    private String getResource(String path) throws IOException {
        final File inputFile = ResourceUtils.getFile(path);

        return Files.lines(inputFile.toPath()).collect(Collectors.joining());
    }

    private AccessTokenDTO createMockedAccessTokenDTO() {
        return AccessTokenDTO.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }
}
