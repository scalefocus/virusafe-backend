package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.virusafe.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {

    @ApiModelProperty(example = "3")
    private Integer id;

    @ApiModelProperty(example = "Difficulty breathing or shortness of breath")
    private String questionTitle;

    @ApiModelProperty(example = "BOOLEAN")
    private QuestionType questionType;

}
