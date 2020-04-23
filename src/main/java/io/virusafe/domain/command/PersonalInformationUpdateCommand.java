package io.virusafe.domain.command;

import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PersonalInformationUpdateCommand {

    private String identificationNumber;

    private IdentificationType identificationType;

    private Integer age;

    private Gender gender;

    private String preExistingConditions;
}
