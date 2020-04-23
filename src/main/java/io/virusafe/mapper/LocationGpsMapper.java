package io.virusafe.mapper;

import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.LocationGpsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper
public interface LocationGpsMapper {

    /**
     * Map a LocationGpsDTO to a LocationDocument, using a provided user GUID.
     *
     * @param userGuid       the user GUID to use
     * @param locationGpsDTO the LocationGpsDTO to map
     * @return the mapped LocationDocument
     */
    @Mapping(source = "userGuid", target = "userGuid")
    @Mapping(source = "locationGpsDTO.location", target = "geoPoint", qualifiedByName = "locationToLocationDto")
    @Mapping(source = "locationGpsDTO.timestamp", target = "timestamp")
    LocationDocument mapToLocationDocument(String userGuid,
                                           LocationGpsDTO locationGpsDTO);

    /**
     * Map a Location to a GeoPoint
     *
     * @param location the Location to map
     * @return the mapped GeoPoint
     */
    @Named("locationToLocationDto")
    default GeoPoint locationToLocationDto(Location location) {
        return new GeoPoint(location.getLat(), location.getLng());
    }

}
