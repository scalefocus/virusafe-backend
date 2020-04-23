package io.virusafe.domain.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.virusafe.domain.Gender;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class StringToGenderConverter extends StdConverter<String, Gender> {

    private static final String CONVERSION_FAILED_MESSAGE = "Could not convert input gender {} due to exception {}";

    @Override
    public Gender convert(final String genderString) {
        // Attempt to convert, returning null on failure rather than throwing an exception.
        try {
            return Gender.valueOf(genderString.toUpperCase(Locale.getDefault()));
        } catch (Exception e) {
            log.debug(CONVERSION_FAILED_MESSAGE, genderString, e.getMessage());
            return null;
        }
    }
}
