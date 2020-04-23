package io.virusafe.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ToString
public class AnswerDTO {

    @ApiModelProperty(example = "3")
    @NotNull(message = "Question ID is required")
    private Integer questionId;

    @ApiModelProperty(example = "TRUE")
    @NotNull(message = "answer is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ,.()а-яА-Я-]+$", message = "Invalid answer format")
    private String answer;
}
