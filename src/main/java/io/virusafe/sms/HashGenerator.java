package io.virusafe.sms;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Generate hash that we need for SMS provider
 */
public interface HashGenerator {
    /**
     * Generate hash using specific algorithm
     *
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    String generateHash(String message) throws NoSuchAlgorithmException, InvalidKeyException;
}
