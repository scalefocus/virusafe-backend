package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProximityDTO {
    @ApiModelProperty(example = SwaggerConstants.DEVICE_UUID)
    @NotNull(message = "uuid is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "invalid pattern format")
    private String uuid;
    @ApiModelProperty(example = SwaggerConstants.DEFAULT_DISTANCE)
    @NotNull(message = "distance is required")
    @Pattern(regexp = "^[\\d]+[.]{0,1}[\\d]+$", message = "invalid distance format")
    private String distance;
}
