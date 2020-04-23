package io.virusafe.validation.personalnumber;

import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@ConditionalOnProperty(value = "validation.strategy.personal-number.passport.enabled")
public class PassportValidator implements PersonalNumberValidatorService {

    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[a-zA-Z0-9]{5,20}$");

    @Override
    public boolean isValidPersonalNumber(final PersonalInformationRequestDTO personalInformationRequestDTO) {
        String personalNumber = personalInformationRequestDTO.getIdentificationNumber();
        if (Objects.isNull(personalNumber) || personalNumber.length() < 5 || personalNumber.length() > 20) {
            return false;
        }
        return PASSPORT_PATTERN.matcher(personalNumber).find();
    }

    @Override
    public IdentificationType getIdentificationType() {
        return IdentificationType.PASSPORT;
    }
}
