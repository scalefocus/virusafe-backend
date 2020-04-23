package io.virusafe.service.pin.generator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "pin.generation.strategy",
        havingValue = "SIX_ZEROES"
)
public class SixZeroesPinGenerator implements PinGenerator {

    private static final String SIX_ZEROES_PIN = "000000";

    @Override
    public String generatePin() {
        return SIX_ZEROES_PIN;
    }
}
