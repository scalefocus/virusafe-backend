package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class LocationGpsDTO {
    @ApiModelProperty(example = SwaggerConstants.DEFAULT_TIMESTAMP)
    @NotNull(message = "Timestamp is required")
    private Long timestamp;
    @NotNull(message = "Location is required")
    @Valid
    private Location location;
}
