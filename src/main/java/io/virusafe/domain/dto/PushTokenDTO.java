package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class PushTokenDTO {

    @ApiModelProperty(example = SwaggerConstants.PUSH_TOKEN)
    @NotNull(message = "pushToken is required")
    @Pattern(regexp = "^[a-zA-Z0-9?=:_-]{0,255}$", message = "pushToken format invalid")
    private String pushToken;
}
