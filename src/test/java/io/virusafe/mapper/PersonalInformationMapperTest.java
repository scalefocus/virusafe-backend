package io.virusafe.mapper;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.dto.PersonalInformationRequestDTO;
import io.virusafe.domain.dto.PersonalInformationResponseDTO;
import io.virusafe.domain.entity.UserDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonalInformationMapperTest {

    private static final String PASSPORT = "PASSPORT";
    private static final String CONDITIONS = "CONDITIONS";
    private static final int AGE = 29;
    private PersonalInformationMapper personalInformationMapper = Mappers.getMapper(PersonalInformationMapper.class);

    @Test
    public void testMapToUpdateCommand() {
        PersonalInformationRequestDTO personalInformationRequestDTO = new PersonalInformationRequestDTO();
        personalInformationRequestDTO.setGender(Gender.FEMALE);
        personalInformationRequestDTO.setAge(AGE);
        personalInformationRequestDTO.setIdentificationType(IdentificationType.PASSPORT);
        personalInformationRequestDTO.setIdentificationNumber(PASSPORT);
        personalInformationRequestDTO.setPreExistingConditions(CONDITIONS);

        PersonalInformationUpdateCommand updateCommand =
                personalInformationMapper.mapToUpdateCommand(personalInformationRequestDTO);
        assertAll(
                () -> assertNotNull(updateCommand),
                () -> assertEquals(AGE, updateCommand.getAge()),
                () -> assertEquals(Gender.FEMALE, updateCommand.getGender()),
                () -> assertEquals(IdentificationType.PASSPORT, updateCommand.getIdentificationType()),
                () -> assertEquals(PASSPORT, updateCommand.getIdentificationNumber()),
                () -> assertEquals(CONDITIONS, updateCommand.getPreExistingConditions())
        );
    }

    @Test
    public void testMapNullToUpdateCommand() {
        PersonalInformationUpdateCommand updateCommand =
                personalInformationMapper.mapToUpdateCommand(null);
        assertNull(updateCommand);
    }

    @Test
    public void testMapToResponseDTO() {
        UserDetails userDetails = UserDetails.builder()
                .age(AGE)
                .gender(Gender.FEMALE)
                .identificationType(IdentificationType.PASSPORT)
                .identificationNumberPlain(PASSPORT)
                .preExistingConditions(CONDITIONS)
                .build();

        PersonalInformationResponseDTO responseDTO = personalInformationMapper.mapToResponseDTO(userDetails);
        assertAll(
                () -> assertNotNull(responseDTO),
                () -> assertEquals(AGE, responseDTO.getAge()),
                () -> assertEquals(Gender.FEMALE, responseDTO.getGender()),
                () -> assertEquals(IdentificationType.PASSPORT, responseDTO.getIdentificationType()),
                () -> assertEquals(PASSPORT, responseDTO.getIdentificationNumber()),
                () -> assertEquals(CONDITIONS, responseDTO.getPreExistingConditions())
        );
    }

    @Test
    public void testMapNullToResponseDTO() {
        PersonalInformationResponseDTO responseDTO = personalInformationMapper.mapToResponseDTO(null);
        assertNull(responseDTO);
    }
}