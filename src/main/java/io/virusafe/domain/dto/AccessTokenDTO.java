package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AccessTokenDTO {

    @ApiModelProperty(example = SwaggerConstants.TOKEN)
    private String accessToken;
    @ApiModelProperty(example = SwaggerConstants.REFRESH_TOKEN)
    private String refreshToken;
}
