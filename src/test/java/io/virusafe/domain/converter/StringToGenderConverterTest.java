package io.virusafe.domain.converter;

import io.virusafe.domain.Gender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringToGenderConverterTest {

    private static final String MALE = "MALE";
    private static final String INVALID = "INVALID";
    private final StringToGenderConverter stringToGenderConverter = new StringToGenderConverter();

    @Test
    public void testConvert() {
        assertEquals(Gender.MALE, stringToGenderConverter.convert(MALE));
    }

    @Test
    public void testConvertInvalidString() {
        assertNull(stringToGenderConverter.convert(INVALID));
    }

    @Test
    public void testConvertNull() {
        assertNull(stringToGenderConverter.convert(null));
    }
}