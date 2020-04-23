package io.virusafe.service.pin.generator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SixDigitRandomPinGeneratorTest {

    private static final int PINS_TO_GENERATE = 10000;
    private static final int PIN_LENGTH = 6;
    private final SixDigitRandomPinGenerator sixDigitRandomPinGenerator = new SixDigitRandomPinGenerator();

    @Test
    public void testGenerateDifferentSixDigitPins() {
        List<String> generatedPins = new ArrayList<>();
        for (int i = 0; i < PINS_TO_GENERATE; i++) {
            String pin = sixDigitRandomPinGenerator.generatePin();
            generatedPins.add(pin);
        }

        assertAll(
                () -> assertEquals(PINS_TO_GENERATE, generatedPins.size()),
                () -> assertEquals(PINS_TO_GENERATE,
                        generatedPins.stream()
                                .filter(p -> PIN_LENGTH == p.length())
                                .count(), "All PINs are of length 6")
        );
    }
}