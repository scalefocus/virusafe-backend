package io.virusafe.service.pin.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SixZeroesPinGeneratorTest {

    private static final String SIX_ZEROES = "000000";
    private final SixZeroesPinGenerator sixZeroesPinGenerator = new SixZeroesPinGenerator();

    @Test
    public void testGeneratePin() {
        String pin = sixZeroesPinGenerator.generatePin();
        assertEquals(SIX_ZEROES, pin);
    }
}