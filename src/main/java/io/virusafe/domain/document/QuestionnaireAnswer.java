package io.virusafe.domain.document;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionnaireAnswer {

    private Integer questionId;

    private String questionTitle;

    private String answer;

    /**
     * All-args constructor for QuestionnaireAnswer.
     * Can be used as a Lombok builder.
     *
     * @param questionId the question's ID
     * @param questionTitle the full question title
     * @param answer the full question answer
     */
    @Builder
    public QuestionnaireAnswer(final Integer questionId, final String questionTitle, final String answer) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.answer = answer;
    }
}
