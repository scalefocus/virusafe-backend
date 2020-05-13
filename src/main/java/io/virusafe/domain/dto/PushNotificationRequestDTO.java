package io.virusafe.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequestDTO {

    @Valid
    @NotNull
    private QuestionnaireQueryDTO questionnaireQuery;

    @NotNull
    @Size(max = 30)
    private String title;

    @NotNull
    @Size(max = 200)
    private String message;

    private boolean reverseQueryResults;
}
