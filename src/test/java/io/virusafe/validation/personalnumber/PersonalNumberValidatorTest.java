package io.virusafe.validation.personalnumber;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class PersonalNumberValidatorTest {

    private final Clock systemClock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );
    private final EgnValidator egnValidator = new EgnValidator(systemClock);
    private final PassportValidator passportValidator = new PassportValidator();
    private final LnchValidator lnchValidator = new LnchValidator();
    private final List<PersonalNumberValidatorService> validators =
            Arrays.asList(egnValidator, passportValidator, lnchValidator);

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private final PersonalNumberValidator personalNumberValidator = new PersonalNumberValidator(validators);
    private final PersonalNumberValidator noValidationValidator = new PersonalNumberValidator(Collections.emptyList());

    @ParameterizedTest
    @MethodSource("providePersonalInformationRequests")
    public void testValidEgnShouldPass(PersonalInformationRequestDTO requestDTO, boolean expected, String message) {
        assertEquals(expected, personalNumberValidator.isValid(requestDTO, constraintValidatorContext), message);
    }

    @Test
    public void testShouldFailWhenNoValidators() {
        PersonalInformationRequestDTO validMale = new PersonalInformationRequestDTO();
        validMale.setIdentificationType(IdentificationType.EGN);
        validMale.setIdentificationNumber("7004306769");
        validMale.setGender(Gender.MALE);
        validMale.setAge(50);
        assertFalse(noValidationValidator.isValid(validMale, constraintValidatorContext));
    }

    private static Stream<Arguments> providePersonalInformationRequests() {
        PersonalInformationRequestDTO validMale = new PersonalInformationRequestDTO();
        validMale.setIdentificationType(IdentificationType.EGN);
        validMale.setIdentificationNumber("7004306769");
        validMale.setGender(Gender.MALE);
        validMale.setAge(50);

        PersonalInformationRequestDTO validMaleUnder20 = new PersonalInformationRequestDTO();
        validMaleUnder20.setIdentificationType(IdentificationType.EGN);
        validMaleUnder20.setIdentificationNumber("0851092922");
        validMaleUnder20.setGender(Gender.MALE);
        validMaleUnder20.setAge(11);


        PersonalInformationRequestDTO validMaleOver120 = new PersonalInformationRequestDTO();
        validMaleOver120.setIdentificationType(IdentificationType.EGN);
        validMaleOver120.setIdentificationNumber("7323143261");
        validMaleOver120.setGender(Gender.MALE);
        validMaleOver120.setAge(147);

        PersonalInformationRequestDTO validFemale = new PersonalInformationRequestDTO();
        validFemale.setIdentificationType(IdentificationType.EGN);
        validFemale.setIdentificationNumber("8607122413");
        validFemale.setGender(Gender.FEMALE);
        validFemale.setAge(34);

        PersonalInformationRequestDTO validFemaleUnder20 = new PersonalInformationRequestDTO();
        validFemaleUnder20.setIdentificationType(IdentificationType.EGN);
        validFemaleUnder20.setIdentificationNumber("1149072990");
        validFemaleUnder20.setGender(Gender.FEMALE);
        validFemaleUnder20.setAge(8);


        PersonalInformationRequestDTO validFemaleOver120 = new PersonalInformationRequestDTO();
        validFemaleOver120.setIdentificationType(IdentificationType.EGN);
        validFemaleOver120.setIdentificationNumber("9224026151");
        validFemaleOver120.setGender(Gender.FEMALE);
        validFemaleOver120.setAge(128);

        PersonalInformationRequestDTO validLnchTypeEGN = new PersonalInformationRequestDTO();
        validLnchTypeEGN.setIdentificationType(IdentificationType.EGN);
        validLnchTypeEGN.setIdentificationNumber("1111111111");
        validLnchTypeEGN.setGender(Gender.MALE);
        validLnchTypeEGN.setAge(148);

        PersonalInformationRequestDTO invalidCheckSum = new PersonalInformationRequestDTO();
        invalidCheckSum.setIdentificationType(IdentificationType.EGN);
        invalidCheckSum.setIdentificationNumber("4905301159");
        invalidCheckSum.setGender(Gender.FEMALE);
        invalidCheckSum.setAge(70);

        PersonalInformationRequestDTO invalidAge = new PersonalInformationRequestDTO();
        invalidAge.setIdentificationType(IdentificationType.EGN);
        invalidAge.setIdentificationNumber("1149072990");
        invalidAge.setGender(Gender.FEMALE);
        invalidAge.setAge(9);

        PersonalInformationRequestDTO invalidGender = new PersonalInformationRequestDTO();
        invalidGender.setIdentificationType(IdentificationType.EGN);
        invalidGender.setIdentificationNumber("1149072990");
        invalidGender.setGender(Gender.MALE);
        invalidGender.setAge(8);

        PersonalInformationRequestDTO monthOver52 = new PersonalInformationRequestDTO();
        monthOver52.setIdentificationType(IdentificationType.EGN);
        monthOver52.setIdentificationNumber("0453114605");

        PersonalInformationRequestDTO monthOver12 = new PersonalInformationRequestDTO();
        monthOver12.setIdentificationType(IdentificationType.EGN);
        monthOver12.setIdentificationNumber("5717112747");

        PersonalInformationRequestDTO monthZero = new PersonalInformationRequestDTO();
        monthZero.setIdentificationType(IdentificationType.EGN);
        monthZero.setIdentificationNumber("8400118944");

        PersonalInformationRequestDTO monthOver32 = new PersonalInformationRequestDTO();
        monthOver32.setIdentificationType(IdentificationType.EGN);
        monthOver32.setIdentificationNumber("2635116625");

        PersonalInformationRequestDTO dayZero = new PersonalInformationRequestDTO();
        dayZero.setIdentificationType(IdentificationType.EGN);
        dayZero.setIdentificationNumber("4804003507");

        PersonalInformationRequestDTO dayOver31 = new PersonalInformationRequestDTO();
        dayOver31.setIdentificationType(IdentificationType.EGN);
        dayOver31.setIdentificationNumber("0845335888");

        PersonalInformationRequestDTO feb29NonLeap = new PersonalInformationRequestDTO();
        feb29NonLeap.setIdentificationType(IdentificationType.EGN);
        feb29NonLeap.setIdentificationNumber("0742292689");

        PersonalInformationRequestDTO validMaleEGN = new PersonalInformationRequestDTO();
        validMaleEGN.setIdentificationType(IdentificationType.LNCH);
        validMaleEGN.setIdentificationNumber("7004306769");

        PersonalInformationRequestDTO validMaleUnder20EGN = new PersonalInformationRequestDTO();
        validMaleUnder20EGN.setIdentificationType(IdentificationType.LNCH);
        validMaleUnder20EGN.setIdentificationNumber("0851092922");

        PersonalInformationRequestDTO validMaleOver120EGN = new PersonalInformationRequestDTO();
        validMaleOver120EGN.setIdentificationType(IdentificationType.LNCH);
        validMaleOver120EGN.setIdentificationNumber("7323143261");

        PersonalInformationRequestDTO validFemaleEGN = new PersonalInformationRequestDTO();
        validFemaleEGN.setIdentificationType(IdentificationType.LNCH);
        validFemaleEGN.setIdentificationNumber("8607122413");

        PersonalInformationRequestDTO validFemaleUnder20EGN = new PersonalInformationRequestDTO();
        validFemaleUnder20EGN.setIdentificationType(IdentificationType.LNCH);
        validFemaleUnder20EGN.setIdentificationNumber("1149072990");

        PersonalInformationRequestDTO validFemaleOver120EGN = new PersonalInformationRequestDTO();
        validFemaleOver120EGN.setIdentificationType(IdentificationType.LNCH);
        validFemaleOver120EGN.setIdentificationNumber("9223144838");

        PersonalInformationRequestDTO validLnch = new PersonalInformationRequestDTO();
        validLnch.setIdentificationType(IdentificationType.LNCH);
        validLnch.setIdentificationNumber("1111111111");

        PersonalInformationRequestDTO invalidEGNCheckSum = new PersonalInformationRequestDTO();
        invalidEGNCheckSum.setIdentificationType(IdentificationType.LNCH);
        invalidEGNCheckSum.setIdentificationNumber("4905301159");

        PersonalInformationRequestDTO invalidEGNAge = new PersonalInformationRequestDTO();
        invalidEGNAge.setIdentificationType(IdentificationType.LNCH);
        invalidEGNAge.setIdentificationNumber("1149072990");

        PersonalInformationRequestDTO invalidEGNGender = new PersonalInformationRequestDTO();
        invalidEGNGender.setIdentificationType(IdentificationType.LNCH);
        invalidEGNGender.setIdentificationNumber("1149072990");

        PersonalInformationRequestDTO eGNmonthOver52 = new PersonalInformationRequestDTO();
        eGNmonthOver52.setIdentificationType(IdentificationType.LNCH);
        eGNmonthOver52.setIdentificationNumber("0453114605");

        PersonalInformationRequestDTO eGNmonthOver12 = new PersonalInformationRequestDTO();
        eGNmonthOver12.setIdentificationType(IdentificationType.LNCH);
        eGNmonthOver12.setIdentificationNumber("5717112747");

        PersonalInformationRequestDTO eGNmonthZero = new PersonalInformationRequestDTO();
        eGNmonthZero.setIdentificationType(IdentificationType.LNCH);
        eGNmonthZero.setIdentificationNumber("8400118944");

        PersonalInformationRequestDTO eGNmonthOver32 = new PersonalInformationRequestDTO();
        eGNmonthOver32.setIdentificationType(IdentificationType.LNCH);
        eGNmonthOver32.setIdentificationNumber("2635116625");

        PersonalInformationRequestDTO eGNdayZero = new PersonalInformationRequestDTO();
        eGNdayZero.setIdentificationType(IdentificationType.LNCH);
        eGNdayZero.setIdentificationNumber("4804003507");

        PersonalInformationRequestDTO eGNdayOver31 = new PersonalInformationRequestDTO();
        eGNdayOver31.setIdentificationType(IdentificationType.LNCH);
        eGNdayOver31.setIdentificationNumber("0845335888");

        PersonalInformationRequestDTO eGNfeb29NonLeap = new PersonalInformationRequestDTO();
        eGNfeb29NonLeap.setIdentificationType(IdentificationType.LNCH);
        eGNfeb29NonLeap.setIdentificationNumber("0742292689");

        PersonalInformationRequestDTO tooShort = new PersonalInformationRequestDTO();
        tooShort.setIdentificationType(IdentificationType.PASSPORT);
        tooShort.setIdentificationNumber("BG12");

        PersonalInformationRequestDTO tooLong = new PersonalInformationRequestDTO();
        tooLong.setIdentificationType(IdentificationType.PASSPORT);
        tooLong.setIdentificationNumber("BG123456789101112BCCa");

        PersonalInformationRequestDTO nonAlpha = new PersonalInformationRequestDTO();
        nonAlpha.setIdentificationType(IdentificationType.PASSPORT);
        nonAlpha.setIdentificationNumber("БГ12345678+1112ВСС");

        PersonalInformationRequestDTO validPassport = new PersonalInformationRequestDTO();
        validPassport.setIdentificationType(IdentificationType.PASSPORT);
        validPassport.setIdentificationNumber("BG12345");

        PersonalInformationRequestDTO missingIdType = new PersonalInformationRequestDTO();
        missingIdType.setIdentificationNumber("1149072990");

        return Stream.of(
                Arguments.of(validMale, true, "Valid male 1900-2000"),
                Arguments.of(validMaleUnder20, true, "Valid male 2000-3000"),
                Arguments.of(validMaleOver120, true, "Valid male 1800-1900"),
                Arguments.of(validFemale, true, "Valid female 1900-2000"),
                Arguments.of(validFemaleUnder20, true, "Valid female 2000-3000"),
                Arguments.of(validFemaleOver120, true, "Valid female 1800-1900"),
                Arguments.of(validLnchTypeEGN, false, "Valid LNCH"),
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
                Arguments.of(tooShort, false, "Too short"),
                Arguments.of(tooLong, false, "Too long"),
                Arguments.of(nonAlpha, false, "Non-alpha"),
                Arguments.of(validPassport, true, "Valid passport"),
                Arguments.of(missingIdType, false, "Missing identification type")
        );
    }
}