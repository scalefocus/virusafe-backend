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
class LnchValidatorTest {
    private final LnchValidator lnchValidator = new LnchValidator();

    @ParameterizedTest
    @MethodSource("providePersonalInformationRequests")
    public void testValidLnchShouldPass(PersonalInformationRequestDTO requestDTO, boolean expected, String message) {
        assertEquals(expected, lnchValidator.isValidPersonalNumber(requestDTO), message);
    }

    private static Stream<Arguments> providePersonalInformationRequests() {
        PersonalInformationRequestDTO validMaleEGN = new PersonalInformationRequestDTO();
        validMaleEGN.setIdentificationNumber("7004306769");
        PersonalInformationRequestDTO validMaleUnder20EGN = new PersonalInformationRequestDTO();
        validMaleUnder20EGN.setIdentificationNumber("0851092922");
        PersonalInformationRequestDTO validMaleOver120EGN = new PersonalInformationRequestDTO();
        validMaleOver120EGN.setIdentificationNumber("7323143261");
        PersonalInformationRequestDTO validFemaleEGN = new PersonalInformationRequestDTO();
        validFemaleEGN.setIdentificationNumber("8607122413");
        PersonalInformationRequestDTO validFemaleUnder20EGN = new PersonalInformationRequestDTO();
        validFemaleUnder20EGN.setIdentificationNumber("1149072990");
        PersonalInformationRequestDTO validFemaleOver120EGN = new PersonalInformationRequestDTO();
        validFemaleOver120EGN.setIdentificationNumber("9223144838");
        PersonalInformationRequestDTO validLnch = new PersonalInformationRequestDTO();
        validLnch.setIdentificationNumber("1111111111");
        PersonalInformationRequestDTO invalidEGNCheckSum = new PersonalInformationRequestDTO();
        invalidEGNCheckSum.setIdentificationNumber("4905301159");
        PersonalInformationRequestDTO invalidEGNAge = new PersonalInformationRequestDTO();
        invalidEGNAge.setIdentificationNumber("1149072990");
        PersonalInformationRequestDTO invalidEGNGender = new PersonalInformationRequestDTO();
        invalidEGNGender.setIdentificationNumber("1149072990");
        PersonalInformationRequestDTO eGNmonthOver52 = new PersonalInformationRequestDTO();
        eGNmonthOver52.setIdentificationNumber("0453114605");
        PersonalInformationRequestDTO eGNmonthOver12 = new PersonalInformationRequestDTO();
        eGNmonthOver12.setIdentificationNumber("5717112747");
        PersonalInformationRequestDTO eGNmonthZero = new PersonalInformationRequestDTO();
        eGNmonthZero.setIdentificationNumber("8400118944");
        PersonalInformationRequestDTO eGNmonthOver32 = new PersonalInformationRequestDTO();
        eGNmonthOver32.setIdentificationNumber("2635116625");
        PersonalInformationRequestDTO eGNdayZero = new PersonalInformationRequestDTO();
        eGNdayZero.setIdentificationNumber("4804003507");
        PersonalInformationRequestDTO eGNdayOver31 = new PersonalInformationRequestDTO();
        eGNdayOver31.setIdentificationNumber("0845335888");
        PersonalInformationRequestDTO eGNfeb29NonLeap = new PersonalInformationRequestDTO();
        eGNfeb29NonLeap.setIdentificationNumber("0742292689");
        PersonalInformationRequestDTO longerNumber = new PersonalInformationRequestDTO();
        longerNumber.setIdentificationNumber("074229268900");
        PersonalInformationRequestDTO nullNumber = new PersonalInformationRequestDTO();

        return Stream.of(
                Arguments.of(validMaleEGN, false, "Valid EGN male 1900-2000"),
                Arguments.of(validMaleUnder20EGN, false, "Valid EGN male 2000-3000"),
                Arguments.of(validMaleOver120EGN, false, "Valid EGN male 1800-1900"),
                Arguments.of(validFemaleEGN, false, "Valid EGN female 1900-2000"),
                Arguments.of(validFemaleUnder20EGN, false, "Valid EGN female 2000-3000"),
                Arguments.of(validFemaleOver120EGN, false, "Valid EGN female 1800-1900"),
                Arguments.of(validLnch, true, "Valid LNCH"),
                Arguments.of(invalidEGNCheckSum, false, "Invalid EGN checksum"),
                Arguments.of(invalidEGNAge, false, "Invalid EGN age"),
                Arguments.of(invalidEGNGender, false, "Invalid EGN gender"),
                Arguments.of(eGNmonthOver52, false, "EGN month over 52"),
                Arguments.of(eGNmonthOver12, false, "EGN month 13-19"),
                Arguments.of(eGNmonthZero, false, "EGN month 0"),
                Arguments.of(eGNmonthOver32, false, "EGN month 33-39"),
                Arguments.of(eGNdayZero, false, "EGN day 0"),
                Arguments.of(eGNdayOver31, false, "EGN day over 31"),
                Arguments.of(eGNfeb29NonLeap, false, "EGN Feb 29th for a non-leap year"),
                Arguments.of(longerNumber, false, "Longer personal number"),
                Arguments.of(nullNumber, false, "Null personal number")
        );
    }
}