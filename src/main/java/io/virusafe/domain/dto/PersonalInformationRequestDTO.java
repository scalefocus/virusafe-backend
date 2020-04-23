package io.virusafe.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import io.virusafe.domain.Gender;
import io.virusafe.domain.IdentificationType;
import io.virusafe.domain.converter.StringToGenderConverter;
import io.virusafe.domain.converter.StringToIdentificationTypeConverter;
import io.virusafe.validation.annotation.ValidPersonalNumber;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
@ValidPersonalNumber
@ToString
public class PersonalInformationRequestDTO {

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_IDENTIFICATION_NUMBER)
    private String identificationNumber;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_IDENTIFICATION_TYPE)
    @JsonDeserialize(converter = StringToIdentificationTypeConverter.class)
    private IdentificationType identificationType;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_AGE)
    @Max(value = 150, message = "age can not be larger than 150")
    @Min(value = 14, message = "age can not be lower than 14")
    private Integer age;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_GENDER)
    @JsonDeserialize(converter = StringToGenderConverter.class)
    private Gender gender;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_EXISTING_CONDITIONS)
    @Pattern(regexp = "^[a-zA-Z0-9 ,.()а-яА-Я-]{0,100}$", message = "Invalid preExistingConditions format")
    private String preExistingConditions;
}
