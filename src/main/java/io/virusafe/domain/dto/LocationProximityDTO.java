package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LocationProximityDTO {
    @ApiModelProperty(example = SwaggerConstants.DEFAULT_TIMESTAMP)
    @NotNull
    private Long timestamp;

    @Valid
    private Location location;

    @Valid
    private List<ProximityDTO> proximities;
}
