package io.virusafe.controller;

import io.virusafe.controller.configuration.AuthenticationPrincipalResolver;
import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.LocationGpsDTO;
import io.virusafe.domain.dto.LocationProximityDTO;
import io.virusafe.domain.dto.ProximityDTO;
import io.virusafe.mapper.LocationGpsMapper;
import io.virusafe.service.location.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LocationControllerTest {

    private static final String BASE_URL = "/location";
    private static final String CREATE_NEW_GPS_LOCATION = "classpath:json/createNewGpsLocation.json";
    private static final String CREATE_NEW_PROXIMITY = "classpath:json/createProximity.json";
    private static final String USER_GUID = "USER_GUID";
    private static final String IDENTIFICATION_NUMBER = "0000000000";

    @Mock
    private LocationService locationService;
    @Mock
    private LocationGpsMapper locationGpsMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new LocationController(locationService, locationGpsMapper))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(USER_GUID, IDENTIFICATION_NUMBER))
                .build();
    }

    @Test
    public void addGpsLocation() throws Exception {
        final String content = getResource(CREATE_NEW_GPS_LOCATION);
        LocationGpsDTO mockedLocationGpsDTO = createMockedLocationGpsDTO();
        LocationDocument mockedLocationDocument = createMockedLocationDocument();
        when(locationGpsMapper.mapToLocationDocument(USER_GUID, mockedLocationGpsDTO))
                .thenReturn(mockedLocationDocument);
        doNothing().when(locationService).createLocation(mockedLocationDocument);

        this.mockMvc.perform(post(BASE_URL + "/gps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void addProximity() throws Exception {
        final String content = getResource(CREATE_NEW_PROXIMITY);
        doNothing().when(locationService).postProximity(USER_GUID, createMockedLocationProximityDTO());

        this.mockMvc.perform(post(BASE_URL + "/proximity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private String getResource(String path) throws IOException {
        final File inputFile = ResourceUtils.getFile(path);

        return Files.lines(inputFile.toPath()).collect(Collectors.joining());
    }

    private LocationGpsDTO createMockedLocationGpsDTO() {
        LocationGpsDTO locationGpsDTO = new LocationGpsDTO();
        locationGpsDTO.setTimestamp(1586250293L);
        Location location = new Location();
        location.setLat(23.0);
        location.setLng(27.0);
        locationGpsDTO.setLocation(location);

        return locationGpsDTO;
    }

    private LocationDocument createMockedLocationDocument() {
        LocationDocument locationDocument = new LocationDocument();
        locationDocument.setGeoPoint(new GeoPoint(23, 27));
        locationDocument.setTimestamp(1586250293L);
        locationDocument.setUserGuid(USER_GUID);
        locationDocument.setId("101");

        return locationDocument;
    }

    private LocationProximityDTO createMockedLocationProximityDTO() {
        LocationProximityDTO locationProximityDTO = new LocationProximityDTO();
        locationProximityDTO.setTimestamp(111222333L);
        Location location = new Location();
        location.setLat(55.75);
        location.setLng(43.21);
        locationProximityDTO.setLocation(location);
        ProximityDTO proximityDTO = new ProximityDTO();
        proximityDTO.setDistance("1.5");
        proximityDTO.setUuid("uuid1");
        locationProximityDTO.setProximities(Collections.singletonList(proximityDTO));

        return locationProximityDTO;
    }
}
