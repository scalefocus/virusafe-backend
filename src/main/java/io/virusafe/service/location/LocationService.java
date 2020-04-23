package io.virusafe.service.location;

import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.LocationProximityDTO;

/**
 * Support location operations
 */
public interface LocationService {
    /**
     * Create GPS location
     *
     * @param locationDocument
     */
    void createLocation(LocationDocument locationDocument);

    /**
     * Post proximity data
     *
     * @param userGuid
     * @param locationProximityDTO
     */
    void postProximity(String userGuid, LocationProximityDTO locationProximityDTO);
}
