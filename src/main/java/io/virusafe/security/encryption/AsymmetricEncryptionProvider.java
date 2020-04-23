package io.virusafe.security.encryption;

/**
 * Encripth using asymmetric algorithm
 */
public interface AsymmetricEncryptionProvider {
    /**
     * Encrypt data
     *
     * @param data
     * @return
     */
    String encrypt(String data);

    /**
     * Decrypt data
     *
     * @param data
     * @return
     */
    String decrypt(String data);
}
