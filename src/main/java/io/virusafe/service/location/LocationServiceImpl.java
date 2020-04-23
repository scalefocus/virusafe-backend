package io.virusafe.service.location;

import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.document.LocationProximityDocument;
import io.virusafe.domain.dto.LocationProximityDTO;
import io.virusafe.mapper.LocationProximityMapper;
import io.virusafe.repository.LocationProximityRepository;
import io.virusafe.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Location service implementation
 */
@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    private final LocationProximityRepository locationProximityRepository;

    private final LocationProximityMapper locationProximityMapper;

    /**
     * Construct a new LocationService, using the autowired beans.
     *
     * @param locationRepository
     * @param locationProximityRepository
     * @param locationProximityMapper
     */
    @Autowired
    public LocationServiceImpl(final LocationRepository locationRepository,
                               final LocationProximityRepository locationProximityRepository,
                               final LocationProximityMapper locationProximityMapper) {
        this.locationRepository = locationRepository;
        this.locationProximityRepository = locationProximityRepository;
        this.locationProximityMapper = locationProximityMapper;
    }

    @Override
    public void createLocation(final LocationDocument locationDocument) {
        log.info("Save new locationDocument with user guid: {}", locationDocument.getUserGuid());
        locationRepository.indexWithoutRefresh(locationDocument);
    }

    @Override
    public void postProximity(final String userGuid, final LocationProximityDTO locationProximityDTO) {
        log.info("Save new locationProximity with user guid: {} and timestamp: {}", userGuid,
                locationProximityDTO.getTimestamp());
        LocationProximityDocument locationProximityDocument = locationProximityMapper
                .mapProximityDTOToProximityDocument(userGuid, locationProximityDTO);

        locationProximityRepository.indexWithoutRefresh(locationProximityDocument);
    }
}
