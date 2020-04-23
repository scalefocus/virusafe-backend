package io.virusafe.validation.personalnumber;

import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PassportValidatorTest {
    private final PassportValidator passportValidator = new PassportValidator();

    @ParameterizedTest
    @MethodSource("providePersonalInformationRequests")
    public void testValidPassportShouldPass(PersonalInformationRequestDTO requestDTO, boolean expected, String message) {
        assertEquals(expected, passportValidator.isValidPersonalNumber(requestDTO), message);
    }

    private static Stream<Arguments> providePersonalInformationRequests() {
        PersonalInformationRequestDTO tooShort = new PersonalInformationRequestDTO();
        tooShort.setIdentificationNumber("BG12");
        PersonalInformationRequestDTO tooLong = new PersonalInformationRequestDTO();
        tooLong.setIdentificationNumber("BG123456789101112BCCa");
        PersonalInformationRequestDTO nonAlpha = new PersonalInformationRequestDTO();
        nonAlpha.setIdentificationNumber("БГ12345678+1112ВСС");
        PersonalInformationRequestDTO validPassport = new PersonalInformationRequestDTO();
        validPassport.setIdentificationNumber("BG12345");
        PersonalInformationRequestDTO nullPassport = new PersonalInformationRequestDTO();

        return Stream.of(
                Arguments.of(tooShort, false, "Too short"),
                Arguments.of(tooLong, false, "Too long"),
                Arguments.of(nonAlpha, false, "Non-alpha"),
                Arguments.of(nullPassport, false, "Null passport"),
                Arguments.of(validPassport, true, "Valid passport")
        );
    }
}