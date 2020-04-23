package io.virusafe.mapper;

import io.virusafe.domain.document.LocationProximityDocument;
import io.virusafe.domain.dto.Location;
import io.virusafe.domain.dto.LocationProximityDTO;
import io.virusafe.domain.dto.ProximityDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationProximityMapperTest {

    private static final String USER_GUID = "USER_GUID";
    public static final double LAT = 5.0;
    public static final double LNG = 1.5;
    public static final String DISTANCE = "2.3";
    private LocationProximityMapper locationGpsMapper = Mappers.getMapper(LocationProximityMapper.class);

    @Test
    public void testMapToProximityDocument() {
        LocationProximityDTO locationProximityDTO = new LocationProximityDTO();
        ProximityDTO proximityDTO = new ProximityDTO();
        proximityDTO.setDistance(DISTANCE);
        String uuid = UUID.randomUUID().toString();
        proximityDTO.setUuid(uuid);
        Location location = new Location();
        location.setLng(LNG);
        location.setLat(LAT);
        locationProximityDTO.setProximities(Collections.singletonList(proximityDTO));
        locationProximityDTO.setTimestamp(1L);
        locationProximityDTO.setLocation(location);
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(USER_GUID, locationProximityDTO);

        assertAll(
                () -> assertNotNull(proximityDocument),
                () -> assertNotNull(proximityDocument.getGeoPoint()),
                () -> assertEquals(LAT, proximityDocument.getGeoPoint().getLat()),
                () -> assertEquals(LNG, proximityDocument.getGeoPoint().getLon()),
                () -> assertEquals(USER_GUID, proximityDocument.getUserGuid()),
                () -> assertNotNull(proximityDocument.getServerTime()),
                () -> assertEquals(DISTANCE, proximityDocument.getProximities().get(0).getDistance()),
                () -> assertEquals(uuid, proximityDocument.getProximities().get(0).getUuid())
        );
    }

    @Test
    public void testMapNullToProximityDocument() {
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(null, null);
        assertAll(
                () -> assertNull(proximityDocument)
        );
    }

    @Test
    public void testMapToProximityDocumentWithNullGuid() {
        LocationProximityDTO locationProximityDTO = new LocationProximityDTO();
        ProximityDTO proximityDTO = new ProximityDTO();
        proximityDTO.setDistance(DISTANCE);
        String uuid = UUID.randomUUID().toString();
        proximityDTO.setUuid(uuid);
        Location location = new Location();
        location.setLng(LNG);
        location.setLat(LAT);
        locationProximityDTO.setProximities(Collections.singletonList(proximityDTO));
        locationProximityDTO.setTimestamp(1L);
        locationProximityDTO.setLocation(location);
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(null, locationProximityDTO);

        assertAll(
                () -> assertNotNull(proximityDocument),
                () -> assertNotNull(proximityDocument.getGeoPoint()),
                () -> assertEquals(LAT, proximityDocument.getGeoPoint().getLat()),
                () -> assertEquals(LNG, proximityDocument.getGeoPoint().getLon()),
                () -> assertNull(proximityDocument.getUserGuid()),
                () -> assertNotNull(proximityDocument.getServerTime()),
                () -> assertEquals(DISTANCE, proximityDocument.getProximities().get(0).getDistance()),
                () -> assertEquals(uuid, proximityDocument.getProximities().get(0).getUuid())
        );
    }

    @Test
    public void testMapToProximityDocumentWithNullLocationProximity() {
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(USER_GUID, null);

        assertAll(
                () -> assertNotNull(proximityDocument),
                () -> assertNull(proximityDocument.getGeoPoint()),
                () -> assertEquals(USER_GUID, proximityDocument.getUserGuid()),
                () -> assertNotNull(proximityDocument.getServerTime()),
                () -> assertNull(proximityDocument.getProximities())
        );
    }

    @Test
    public void testMapToProximityDocumentWithNullProximities() {
        LocationProximityDTO locationProximityDTO = new LocationProximityDTO();
        Location location = new Location();
        location.setLng(LNG);
        location.setLat(LAT);
        locationProximityDTO.setProximities(null);
        locationProximityDTO.setTimestamp(1L);
        locationProximityDTO.setLocation(location);
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(USER_GUID, locationProximityDTO);

        assertAll(
                () -> assertNotNull(proximityDocument),
                () -> assertNotNull(proximityDocument.getGeoPoint()),
                () -> assertEquals(LAT, proximityDocument.getGeoPoint().getLat()),
                () -> assertEquals(LNG, proximityDocument.getGeoPoint().getLon()),
                () -> assertEquals(USER_GUID, proximityDocument.getUserGuid()),
                () -> assertNotNull(proximityDocument.getServerTime()),
                () -> assertNull(proximityDocument.getProximities())
        );
    }

    @Test
    public void testMapToProximityDocumentWithNullProximityValues() {
        LocationProximityDTO locationProximityDTO = new LocationProximityDTO();
        Location location = new Location();
        location.setLng(LNG);
        location.setLat(LAT);
        locationProximityDTO.setProximities(Collections.singletonList(null));
        locationProximityDTO.setTimestamp(1L);
        locationProximityDTO.setLocation(location);
        LocationProximityDocument proximityDocument =
                locationGpsMapper.mapProximityDTOToProximityDocument(USER_GUID, locationProximityDTO);

        assertAll(
                () -> assertNotNull(proximityDocument),
                () -> assertNotNull(proximityDocument.getGeoPoint()),
                () -> assertEquals(LAT, proximityDocument.getGeoPoint().getLat()),
                () -> assertEquals(LNG, proximityDocument.getGeoPoint().getLon()),
                () -> assertEquals(USER_GUID, proximityDocument.getUserGuid()),
                () -> assertNotNull(proximityDocument.getServerTime()),
                () -> assertNotNull(proximityDocument.getProximities()),
                () -> assertNull(proximityDocument.getProximities().get(0))
        );
    }

}