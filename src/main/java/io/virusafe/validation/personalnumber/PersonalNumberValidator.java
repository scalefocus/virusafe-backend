package io.virusafe.validation.personalnumber;

import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import io.virusafe.validation.annotation.ValidPersonalNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PersonalNumberValidator implements ConstraintValidator<ValidPersonalNumber, PersonalInformationRequestDTO> {

    private final Map<IdentificationType, PersonalNumberValidatorService> personalNumberValidatorServices;

    /**
     * Composite DTO a validator with list of appropriate validators
     *
     * @param personalNumberValidatorServices
     */
    @Autowired
    public PersonalNumberValidator(final List<PersonalNumberValidatorService> personalNumberValidatorServices) {
        Map<IdentificationType, PersonalNumberValidatorService> validators = personalNumberValidatorServices.stream()
                .collect(Collectors
                        .toMap(PersonalNumberValidatorService::getIdentificationType, Function.identity()));
        this.personalNumberValidatorServices = Collections.unmodifiableMap(validators);
    }

    @Override
    public boolean isValid(final PersonalInformationRequestDTO personalInformationRequestDTO,
                           final ConstraintValidatorContext constraintValidatorContext) {
        IdentificationType identificationType = personalInformationRequestDTO.getIdentificationType();
        if (Objects.isNull(identificationType) ||
                Objects.isNull(personalNumberValidatorServices.get(identificationType))) {
            return false;
        }
        return personalNumberValidatorServices.get(identificationType)
                .isValidPersonalNumber(personalInformationRequestDTO);
    }
}
