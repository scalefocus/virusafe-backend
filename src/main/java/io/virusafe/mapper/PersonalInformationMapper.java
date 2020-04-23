package io.virusafe.mapper;


import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import io.virusafe.domain.dto.PersonalInformationResponseDTO;
import io.virusafe.domain.entity.UserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PersonalInformationMapper {

    /**
     * Map a personalInformationRequestDTO to a PersonalInformationUpdateCommand.
     *
     * @param personalInformationRequestDTO the PersonalInformationRequestDTO to map
     * @return the mapped PersonalInformationUpdateCommand
     */
    PersonalInformationUpdateCommand mapToUpdateCommand(PersonalInformationRequestDTO personalInformationRequestDTO);

    /**
     * Map UserDetails to a PersonalInformationResponseDTO, using decrypted properties where needed.
     *
     * @param dbUser the UserDetails to map
     * @return the mapped PersonalInformationResponseDTO
     */
    @Mapping(source = "identificationNumberPlain", target = "identificationNumber")
    PersonalInformationResponseDTO mapToResponseDTO(UserDetails dbUser);
}
