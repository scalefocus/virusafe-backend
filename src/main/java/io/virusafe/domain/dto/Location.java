package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class Location {
    @ApiModelProperty(example = SwaggerConstants.DEFAULT_LAT)
    @NotNull
    @Max(value = 90, message = "Lat can not be larger than 90")
    @Min(value = -90, message = "Lat can not be smaller than -90")
    private Double lat;

    @ApiModelProperty(example = SwaggerConstants.DEFAULT_LNG)
    @NotNull
    @Max(value = 180, message = "Lng can not be larger than 180")
    @Min(value = -180, message = "Lng can not be smaller than -180")
    private Double lng;
}
