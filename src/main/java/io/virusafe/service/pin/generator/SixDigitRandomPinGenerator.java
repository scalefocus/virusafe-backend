package io.virusafe.service.pin.generator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@ConditionalOnProperty(
        value = "pin.generation.strategy",
        havingValue = "SIX_DIGITS"
)
public class SixDigitRandomPinGenerator implements PinGenerator {

    private static final int BOUND = 999_999;

    @Override
    public String generatePin() {

        int randomNumber = ThreadLocalRandom.current().nextInt(BOUND);
        // Convert random number to six digit string
        return String.format("%06d", randomNumber);
    }
}
