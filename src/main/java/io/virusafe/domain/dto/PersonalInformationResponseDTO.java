package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import lombok.Data;

@Data
public class PersonalInformationResponseDTO {

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_PHONE_NUMBER)
    private String phoneNumber;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_IDENTIFICATION_NUMBER)
    private String identificationNumber;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_IDENTIFICATION_TYPE)
    private IdentificationType identificationType;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_AGE)
    private Integer age;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_GENDER)
    private Gender gender;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_EXISTING_CONDITIONS)
    private String preExistingConditions;
}
