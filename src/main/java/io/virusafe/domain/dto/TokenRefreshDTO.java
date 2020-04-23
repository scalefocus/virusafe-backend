package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TokenRefreshDTO {

    @ApiModelProperty(example = SwaggerConstants.REFRESH_TOKEN)
    @NotNull(message = "Refresh token is required")
    private String refreshToken;
}
