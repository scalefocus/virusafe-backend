package io.virusafe.service.pin;

/**
 * Pin operations
 */
public interface PinService {
    /**
     * generate pin
     *
     * @param phoneNumber
     */
    void generatePin(String phoneNumber);

    /**
     * Verify provided by user pin
     *
     * @param phoneNumber
     * @param pin
     * @return
     */
    boolean verifyPin(String phoneNumber, String pin);

    /**
     * invalidate pins for specific user
     *
     * @param phoneNumber
     */
    void invalidatePins(String phoneNumber);
}
