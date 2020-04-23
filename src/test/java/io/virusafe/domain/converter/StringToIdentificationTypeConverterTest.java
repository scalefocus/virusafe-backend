package io.virusafe.domain.converter;

import io.virusafe.domain.IdentificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringToIdentificationTypeConverterTest {

    private static final String PASSPORT = "PASSPORT";
    private static final String INVALID = "INVALID";
    private final StringToIdentificationTypeConverter stringToIdentificationTypeConverter =
            new StringToIdentificationTypeConverter();

    @Test
    public void testConvert() {
        assertEquals(IdentificationType.PASSPORT, stringToIdentificationTypeConverter.convert(PASSPORT));
    }

    @Test
    public void testConvertInvalidString() {
        assertNull(stringToIdentificationTypeConverter.convert(INVALID));
    }

    @Test
    public void testConvertNull() {
        assertNull(stringToIdentificationTypeConverter.convert(null));
    }
}