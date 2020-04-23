package io.virusafe.mapper;

import io.virusafe.domain.document.LocationDocument;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.LocationGpsDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationGpsMapperTest {

    public static final long CURRENT_TIME = System.currentTimeMillis();
    public static final double LAT = 23.74;
    public static final double LNG = 73.56;
    public static final String USER_GUID = "user_guid";

    private LocationGpsMapper locationGpsMapper = Mappers.getMapper(LocationGpsMapper.class);

    @Test
    void mapToLocationDocument() {
        LocationGpsDTO locationGpsDTO = new LocationGpsDTO();
        locationGpsDTO.setTimestamp(CURRENT_TIME);
        Location location = new Location();
        location.setLat(LAT);
        location.setLng(LNG);
        locationGpsDTO.setLocation(location);
        LocationDocument locationGps = locationGpsMapper
                .mapToLocationDocument(USER_GUID, locationGpsDTO);

        assertAll(
                () -> assertNotNull(locationGps),
                () -> assertEquals(USER_GUID, locationGps.getUserGuid()),
                () -> assertNotNull(locationGps.getGeoPoint()),
                () -> assertEquals(LAT, locationGps.getGeoPoint().getLat()),
                () -> assertEquals(LNG, locationGps.getGeoPoint().getLon()),
                () -> assertNotNull(locationGps.getTimestamp()),
                () -> assertNotNull(locationGps.getServerTime())
        );
    }

    @Test
    void mapNullToLocationDocument() {
        LocationDocument locationGps = locationGpsMapper
                .mapToLocationDocument(null, null);

        assertAll(
                () -> assertNull(locationGps)
        );
    }

    @Test
    void mapToLocationDocumentWithNullGuid() {
        LocationGpsDTO locationGpsDTO = new LocationGpsDTO();
        locationGpsDTO.setTimestamp(CURRENT_TIME);
        Location location = new Location();
        location.setLat(LAT);
        location.setLng(LNG);
        locationGpsDTO.setLocation(location);
        LocationDocument locationGps = locationGpsMapper
                .mapToLocationDocument(null, locationGpsDTO);

        assertAll(
                () -> assertNotNull(locationGps),
                () -> assertNull(locationGps.getUserGuid()),
                () -> assertNotNull(locationGps.getGeoPoint()),
                () -> assertEquals(LAT, locationGps.getGeoPoint().getLat()),
                () -> assertEquals(LNG, locationGps.getGeoPoint().getLon()),
                () -> assertNotNull(locationGps.getTimestamp()),
                () -> assertNotNull(locationGps.getServerTime())
        );
    }

    @Test
    void mapToLocationDocumentWithNullLocation() {
        LocationDocument locationGps = locationGpsMapper
                .mapToLocationDocument(USER_GUID, null);

        assertAll(
                () -> assertNotNull(locationGps),
                () -> assertEquals(USER_GUID, locationGps.getUserGuid()),
                () -> assertNull(locationGps.getGeoPoint()),
                () -> assertNull(locationGps.getTimestamp()),
                () -> assertNotNull(locationGps.getServerTime())
        );
    }
}