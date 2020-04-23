package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.configuration.SwaggerConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionnairePostDTO {
    @ApiModelProperty(example = SwaggerConstants.DEFAULT_TIMESTAMP)
    @NotNull(message = "Timestamp is required")
    private Long timestamp;

    @Valid
    private Location location;

    @NotEmpty(message = "Answers are required")
    @Valid
    private List<AnswerDTO> answers;

}
