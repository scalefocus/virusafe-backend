package io.virusafe.validation.personalnumber;

import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "validation.strategy.personal-number.lnch.enabled")
public class LnchValidator implements PersonalNumberValidatorService {

    private static final List<Integer> LNCH_WEIGHTS = Arrays.asList(21, 19, 17, 13, 11, 9, 7, 3, 1);
    private static final Integer LNCH_MOD = 10;

    @Override
    public boolean isValidPersonalNumber(final PersonalInformationRequestDTO personalInformationRequestDTO) {
        String personalNumber = personalInformationRequestDTO.getIdentificationNumber();
        if (Objects.isNull(personalNumber) || personalNumber.length() != 10) {
            return false;
        }

        final List<Integer> lnchDigits = personalNumber
                .chars()
                .map(c -> c - '0')
                .boxed()
                .collect(Collectors.toList());

        int checkSum = 0;
        for (int i = 0; i < lnchDigits.size() - 1; i++) {
            checkSum += lnchDigits.get(i) * LNCH_WEIGHTS.get(i);
        }
        checkSum %= LNCH_MOD;
        return lnchDigits.get(9) == checkSum;
    }

    @Override
    public IdentificationType getIdentificationType() {
        return IdentificationType.LNCH;
    }
}
