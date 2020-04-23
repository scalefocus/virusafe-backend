package io.virusafe.service;

import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.LocationProximityDTO;
import io.virusafe.mapper.LocationProximityMapper;
import io.virusafe.repository.LocationProximityRepository;
import io.virusafe.repository.LocationRepository;
import io.virusafe.service.location.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    private static final String USER_GUID = "USER_GUID";

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationProximityRepository locationProximityRepository;
    @Mock
    private LocationProximityMapper locationProximityMapper;

    private LocationServiceImpl locationService;

    private LocationDocument locationDocument = new LocationDocument();
    private LocationProximityDTO locationProximityDTO = new LocationProximityDTO();

    @BeforeEach
    public void setUp() {
        this.locationService = new LocationServiceImpl(locationRepository,
                locationProximityRepository, locationProximityMapper);
    }

    @Test
    public void testCreateLocationWontRefreshIndex() {
        locationService.createLocation(locationDocument);
        verify(locationRepository, times(1)).indexWithoutRefresh(any());
    }

    @Test
    public void testPostProximityWontRefreshIndex() {
        locationService.postProximity(USER_GUID, locationProximityDTO);
        verify(locationProximityRepository, times(1)).indexWithoutRefresh(any());
    }
}