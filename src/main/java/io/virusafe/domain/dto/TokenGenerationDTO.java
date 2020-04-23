package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TokenGenerationDTO {

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_PHONE_NUMBER)
    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "[\\+]{0,1}[\\d]{10,14}", message = "Invalid phone number format")
    private String phoneNumber;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_PIN)
    @NotNull(message = "PIN is required")
    @Pattern(regexp = "[\\d]{6}", message = "Invalid PIN")
    private String pin;
}
