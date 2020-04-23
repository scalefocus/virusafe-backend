package io.virusafe.domain.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.virusafe.domain.IdentificationType;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class StringToIdentificationTypeConverter extends StdConverter<String, IdentificationType> {

    private static final String CONVERSION_FAILED_MESSAGE = "Could not convert input identification type {} due to exception {}";

    @Override
    public IdentificationType convert(final String identificationType) {
        // Attempt to convert, returning null on failure rather than throwing an exception.
        try {
            return IdentificationType.valueOf(identificationType.toUpperCase(Locale.getDefault()));
        } catch (Exception e) {
            log.debug(CONVERSION_FAILED_MESSAGE, identificationType, e.getMessage());
            return null;
        }
    }
}
