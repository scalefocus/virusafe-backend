package io.virusafe.domain.document;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(indexName = "questionnaire", type = "questionnaire", refreshInterval = "30s")
@Data
@NoArgsConstructor
public class QuestionnaireDocument {

    @Id
    private String id;

    private String userGuid;

    @GeoPointField
    private GeoPoint geoPoint;

    @Field(type = FieldType.Date)
    private Long timestamp;

    @Field(type = FieldType.Date)
    private Long serverTime;

    @Field(type = FieldType.Nested)
    private List<QuestionnaireAnswer> answers = new ArrayList<>();

    /**
     * All-args constructor for QuestionnaireDocument.
     * Can be used as a Lombok builder.
     *
     * @param id         the document ID
     * @param userGuid   the submitting user's GUID
     * @param geoPoint   the submitting user's GeoPoint location
     * @param timestamp  the submitting user's local timestamp
     * @param serverTime the server timestamp for the submission
     * @param answers    the list of QuestionnaireAnswers
     */
    @Builder
    public QuestionnaireDocument(final String id,
                                 final String userGuid,
                                 final GeoPoint geoPoint,
                                 final Long timestamp,
                                 final Long serverTime,
                                 final List<QuestionnaireAnswer> answers) {
        this.id = id;
        this.userGuid = userGuid;
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.serverTime = serverTime;
        if (Objects.nonNull(answers)) {
            this.answers.addAll(answers);
        }
    }

}
