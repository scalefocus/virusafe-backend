package io.virusafe.validation.personalnumber;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Validate EGN using a standard algorithm
 * Validate:
 * - age
 * - gender
 * - check sum
 */
@Service
@ConditionalOnProperty(value = "validation.strategy.personal-number.egn.enabled")
public class EgnValidator implements PersonalNumberValidatorService {

    private static final List<Integer> EGN_WEIGHTS = Arrays.asList(2, 4, 8, 5, 10, 9, 7, 3, 6);
    private static final Integer EGN_MOD = 11;
    private static final int MONTH_AFTER_2000 = 40;
    private static final int MONTH_BEFORE_1900 = 20;
    private static final int CHECK_SUM_10 = 10;

    private final Clock systemClock;

    /**
     * create validator using system clock to make it easier to test
     *
     * @param systemClock
     */
    @Autowired
    public EgnValidator(final Clock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public boolean isValidPersonalNumber(final PersonalInformationRequestDTO personalInformationRequestDTO) {

        String personalNumber = personalInformationRequestDTO.getIdentificationNumber();
        if (Objects.isNull(personalNumber) || personalNumber.length() != 10) {
            return false;
        }

        final List<Integer> personalNumberDigits = personalNumber
                .chars()
                .map(c -> c - '0')
                .boxed()
                .collect(Collectors.toList());

        Integer age = personalInformationRequestDTO.getAge();
        Gender gender = personalInformationRequestDTO.getGender();
        return validateEgnCheckSum(personalNumberDigits) &&
                validateBirthday(personalNumberDigits, age) &&
                validateGender(personalNumberDigits, gender);
    }

    @Override
    public IdentificationType getIdentificationType() {
        return IdentificationType.EGN;
    }

    private boolean validateGender(final List<Integer> personalNumberDigits, final Gender gender) {
        Gender calculatedGender = null;
        if (personalNumberDigits.get(8) % 2 == 0) {
            calculatedGender = Gender.MALE;
        } else {
            calculatedGender = Gender.FEMALE;
        }
        return Objects.isNull(gender) || calculatedGender.equals(gender);
    }

    private boolean validateBirthday(final List<Integer> egnDigits, final Integer age) {
        int year = egnDigits.get(0) * 10 + egnDigits.get(1);
        int month = egnDigits.get(2) * 10 + egnDigits.get(3);
        int day = egnDigits.get(4) * 10 + egnDigits.get(5);

        // Handle month offsets based on birth years before 1900 and after 2000.
        if (month > MONTH_AFTER_2000) {
            month -= 40;
            year += 2000;
        } else if (month > MONTH_BEFORE_1900) {
            month -= 20;
            year += 1800;
        } else {
            year += 1900;
        }
        try {
            LocalDate birthday = LocalDate.of(year, month, day);
            LocalDate today = LocalDate.now(systemClock);
            Integer calculatedAge = Period.between(birthday, today).getYears();
            return Objects.isNull(age) || calculatedAge.equals(age);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateEgnCheckSum(final List<Integer> egnDigits) {
        int checkSum = 0;
        for (int i = 0; i < egnDigits.size() - 1; i++) {
            checkSum += egnDigits.get(i) * EGN_WEIGHTS.get(i);
        }
        checkSum %= EGN_MOD;
        if (checkSum == CHECK_SUM_10) {
            checkSum = 0;
        }
        return egnDigits.get(9) == checkSum;
    }
}
