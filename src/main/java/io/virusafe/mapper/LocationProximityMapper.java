package io.virusafe.mapper;

import io.virusafe.domain.document.LocationProximityDocument;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.LocationProximityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper
public interface LocationProximityMapper {

    /**
     * Map a LocationProximityDTO to a LocationProximityDocument, using the provided user GUID.
     *
     * @param userGuid             the user GUID to use
     * @param locationProximityDTO the LocationProximityDTO to map
     * @return the mapped LocationProximityDocument
     */
    @Mapping(source = "userGuid", target = "userGuid")
    @Mapping(source = "locationProximityDTO.location", target = "geoPoint", qualifiedByName = "locationToLocationDto")
    LocationProximityDocument mapProximityDTOToProximityDocument(String userGuid,
                                                                 LocationProximityDTO locationProximityDTO);

    /**
     * Map a Location to a GeoPoint.
     *
     * @param location the Location to map
     * @return the mapped GeoPoint
     */
    @Named("locationToLocationDto")
    default GeoPoint locationToLocationDto(Location location) {
        return new GeoPoint(location.getLat(), location.getLng());
    }
}
