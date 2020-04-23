package io.virusafe.validation.personalnumber;

import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;

/**
 * Service that validate personal number
 */
public interface PersonalNumberValidatorService {
    /**
     * Validate personal number
     *
     * @param personalInformationRequestDTO
     * @return
     */
    boolean isValidPersonalNumber(PersonalInformationRequestDTO personalInformationRequestDTO);

    /**
     * @return identification type
     */
    IdentificationType getIdentificationType();
}
