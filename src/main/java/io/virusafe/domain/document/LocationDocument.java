package io.virusafe.domain.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@ToString
@Getter
@Setter
@Document(indexName = "location", type = "location", refreshInterval = "30s")
public class LocationDocument {

    @Id
    private String id;

    @GeoPointField
    private GeoPoint geoPoint;

    private String userGuid;

    @Field(type = FieldType.Date)
    private Long timestamp;

    @Field(type = FieldType.Date)
    private Long serverTime = System.currentTimeMillis();

}
