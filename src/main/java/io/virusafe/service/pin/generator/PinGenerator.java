package io.virusafe.service.pin.generator;

/**
 * Generate pin provider
 */
public interface PinGenerator {

    /**
     * Generate pin
     *
     * @return
     */
    String generatePin();
}
