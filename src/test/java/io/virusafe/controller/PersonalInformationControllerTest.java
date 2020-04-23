package io.virusafe.controller;

import io.virusafe.controller.configuration.AuthenticationPrincipalResolver;
import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import io.virusafe.domain.dto.PersonalInformationResponseDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.handler.GlobalExceptionHandler;
import io.virusafe.mapper.PersonalInformationMapper;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PersonalInformationControllerTest {

    private static final String BASE_URL = "/personalinfo";
    private static final String CREATE_PERSONAL_INFO = "classpath:json/createPersonalInfo.json";
    private static final String GET_PERSONAL_INFO = "classpath:json/getPersonalInfo.json";
    private static final String USER_GUID = "USER_GUID";
    private static final String IDENTIFICATION_NUMBER = "0000000000";

    private MockMvc mockMvc;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PersonalInformationMapper personalInformationMapper;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PersonalInformationController(userDetailsService, personalInformationMapper))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, IDENTIFICATION_NUMBER))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    public void addPersonalInformation() throws Exception {

        final String content = getResource(CREATE_PERSONAL_INFO);
        PersonalInformationRequestDTO mockedPersonalInfoRequestDTO = createMockedPersonalInfoRequestDTO();
        PersonalInformationUpdateCommand mockedPersonalInfoCommand = createMockedPersonalInfoCommand();
        when(personalInformationMapper.mapToUpdateCommand(mockedPersonalInfoRequestDTO))
                .thenReturn(mockedPersonalInfoCommand);
        doNothing().when(userDetailsService).updatePersonalInformation(USER_GUID, mockedPersonalInfoCommand);

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void getPersonalInformation() throws Exception {
        final String responseBody = getResource(GET_PERSONAL_INFO);

        UserDetails mockedUserDetails = createMockedUserDetails();
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(mockedUserDetails));
        when(personalInformationMapper.mapToResponseDTO(mockedUserDetails))
                .thenReturn(createMockedPersonalInfoResponseDTO());

        this.mockMvc.perform(get(BASE_URL))
                .andExpect(content().json(responseBody));
    }


    @Test
    public void getPersonalInformationReturnsNotFoundForMissingUser() throws Exception {
        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());

        this.mockMvc.perform(get(BASE_URL))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void deletePersonalInformation() throws Exception {
        doNothing().when(userDetailsService).deleteByUserGuid(USER_GUID);

        this.mockMvc.perform(delete(BASE_URL))
                .andExpect(status().isOk());
    }

    private UserDetails createMockedUserDetails() {
        return UserDetails.builder().userGuid(USER_GUID).build();
    }

    private PersonalInformationResponseDTO createMockedPersonalInfoResponseDTO() {
        PersonalInformationResponseDTO personalInformationResponseDTO = new PersonalInformationResponseDTO();
        personalInformationResponseDTO.setIdentificationNumber(USER_GUID);

        return personalInformationResponseDTO;
    }

    private PersonalInformationRequestDTO createMockedPersonalInfoRequestDTO() {
        PersonalInformationRequestDTO personalInformationRequestDTO = new PersonalInformationRequestDTO();
        personalInformationRequestDTO.setIdentificationNumber("7004306769");
        personalInformationRequestDTO.setIdentificationType(IdentificationType.EGN);
        personalInformationRequestDTO.setAge(5000);
        personalInformationRequestDTO.setGender(Gender.MALE);

        return personalInformationRequestDTO;
    }

    private PersonalInformationUpdateCommand createMockedPersonalInfoCommand() {
        PersonalInformationUpdateCommand personalInformationUpdateCommand = new PersonalInformationUpdateCommand();
        personalInformationUpdateCommand.setAge(5000);
        personalInformationUpdateCommand.setGender(Gender.MALE);
        personalInformationUpdateCommand.setIdentificationNumber("7004306769");
        personalInformationUpdateCommand.setIdentificationType(IdentificationType.EGN);

        return personalInformationUpdateCommand;
    }

    private String getResource(String path) throws IOException {
        final File inputFile = ResourceUtils.getFile(path);

        return Files.lines(inputFile.toPath()).collect(Collectors.joining());
    }
}
