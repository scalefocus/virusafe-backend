package io.virusafe.domain.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Document(indexName = "proximity", type = "proximity", refreshInterval = "30s")
@Data
@NoArgsConstructor
public class LocationProximityDocument {

    @Id
    private String id;

    @Field(type = FieldType.Date)
    private Long timestamp;

    @Field(type = FieldType.Date)
    private Long serverTime = System.currentTimeMillis();

    @GeoPointField
    private GeoPoint geoPoint;

    @Field(type = FieldType.Nested)
    private List<LocationProximity> proximities;

    private String userGuid;
}
