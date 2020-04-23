package io.virusafe.validation.personalnumber;

import io.virusafe.domain.Gender;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EgnValidatorTest {

    private final Clock systemClock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );
    private final EgnValidator egnValidator = new EgnValidator(systemClock);

    @ParameterizedTest
    @MethodSource("providePersonalInformationRequests")
    public void testValidEgnShouldPass(PersonalInformationRequestDTO requestDTO, boolean expected, String message) {
        assertEquals(expected, egnValidator.isValidPersonalNumber(requestDTO), message);
    }

    private static Stream<Arguments> providePersonalInformationRequests() {
        PersonalInformationRequestDTO validMale = new PersonalInformationRequestDTO();
        validMale.setIdentificationNumber("7004306769");
        validMale.setGender(Gender.MALE);
        validMale.setAge(50);

        PersonalInformationRequestDTO validMaleNullGender = new PersonalInformationRequestDTO();
        validMaleNullGender.setIdentificationNumber("7004306769");
        validMaleNullGender.setAge(50);

        PersonalInformationRequestDTO validMaleNullAge = new PersonalInformationRequestDTO();
        validMaleNullAge.setIdentificationNumber("7004306769");
        validMaleNullAge.setGender(Gender.MALE);

        PersonalInformationRequestDTO validMaleUnder20 = new PersonalInformationRequestDTO();
        validMaleUnder20.setIdentificationNumber("0851092922");
        validMaleUnder20.setGender(Gender.MALE);
        validMaleUnder20.setAge(11);

        PersonalInformationRequestDTO validMaleOver120 = new PersonalInformationRequestDTO();
        validMaleOver120.setIdentificationNumber("7323143261");
        validMaleOver120.setGender(Gender.MALE);
        validMaleOver120.setAge(147);

        PersonalInformationRequestDTO validFemale = new PersonalInformationRequestDTO();
        validFemale.setIdentificationNumber("8607122413");
        validFemale.setGender(Gender.FEMALE);
        validFemale.setAge(34);

        PersonalInformationRequestDTO validFemaleUnder20 = new PersonalInformationRequestDTO();
        validFemaleUnder20.setIdentificationNumber("1149072990");
        validFemaleUnder20.setGender(Gender.FEMALE);
        validFemaleUnder20.setAge(8);


        PersonalInformationRequestDTO validFemaleOver120 = new PersonalInformationRequestDTO();
        validFemaleOver120.setIdentificationNumber("9224026151");
        validFemaleOver120.setGender(Gender.FEMALE);
        validFemaleOver120.setAge(128);

        PersonalInformationRequestDTO validLnch = new PersonalInformationRequestDTO();
        validLnch.setIdentificationNumber("1111111111");
        validLnch.setGender(Gender.MALE);
        validLnch.setAge(148);

        PersonalInformationRequestDTO invalidCheckSum = new PersonalInformationRequestDTO();
        invalidCheckSum.setIdentificationNumber("4905301159");
        invalidCheckSum.setGender(Gender.FEMALE);
        invalidCheckSum.setAge(70);

        PersonalInformationRequestDTO invalidAge = new PersonalInformationRequestDTO();
        invalidAge.setIdentificationNumber("1149072990");
        invalidAge.setGender(Gender.FEMALE);
        invalidAge.setAge(9);

        PersonalInformationRequestDTO invalidGender = new PersonalInformationRequestDTO();
        invalidGender.setIdentificationNumber("1149072990");
        invalidGender.setGender(Gender.MALE);
        invalidGender.setAge(8);

        PersonalInformationRequestDTO monthOver52 = new PersonalInformationRequestDTO();
        monthOver52.setIdentificationNumber("0453114605");

        PersonalInformationRequestDTO monthOver12 = new PersonalInformationRequestDTO();
        monthOver12.setIdentificationNumber("5717112747");

        PersonalInformationRequestDTO monthZero = new PersonalInformationRequestDTO();
        monthZero.setIdentificationNumber("8400118944");

        PersonalInformationRequestDTO monthOver32 = new PersonalInformationRequestDTO();
        monthOver32.setIdentificationNumber("2635116625");

        PersonalInformationRequestDTO dayZero = new PersonalInformationRequestDTO();
        dayZero.setIdentificationNumber("4804003507");

        PersonalInformationRequestDTO dayOver31 = new PersonalInformationRequestDTO();
        dayOver31.setIdentificationNumber("0845335888");

        PersonalInformationRequestDTO feb29NonLeap = new PersonalInformationRequestDTO();
        feb29NonLeap.setIdentificationNumber("0742292689");

        PersonalInformationRequestDTO longerNumber = new PersonalInformationRequestDTO();
        longerNumber.setIdentificationNumber("074229268900");

        PersonalInformationRequestDTO nullNumber = new PersonalInformationRequestDTO();

        return Stream.of(
                Arguments.of(validMale, true, "Valid male 1900-2000"),
                Arguments.of(validMaleNullGender, true, "Valid male null gender"),
                Arguments.of(validMaleNullAge, true, "Valid male null age"),
                Arguments.of(validMaleUnder20, true, "Valid male 2000-3000"),
                Arguments.of(validMaleOver120, true, "Valid male 1800-1900"),
                Arguments.of(validFemale, true, "Valid female 1900-2000"),
                Arguments.of(validFemaleUnder20, true, "Valid female 2000-3000"),
                Arguments.of(validFemaleOver120, true, "Valid female 1800-1900"),
                Arguments.of(validLnch, false, "Valid LNCH"),
                Arguments.of(invalidCheckSum, false, "Invalid checksum"),
                Arguments.of(invalidAge, false, "Invalid age"),
                Arguments.of(invalidGender, false, "Invalid gender"),
                Arguments.of(monthOver52, false, "Month over 52"),
                Arguments.of(monthOver12, false, "Month 13-19"),
                Arguments.of(monthZero, false, "Month 0"),
                Arguments.of(monthOver32, false, "Month 33-39"),
                Arguments.of(dayZero, false, "Day 0"),
                Arguments.of(dayOver31, false, "Day over 31"),
                Arguments.of(feb29NonLeap, false, "Feb 29th for a non-leap year"),
                Arguments.of(longerNumber, false, "Longer personal number"),
                Arguments.of(nullNumber, false, "Null personal number")
        );
    }
}