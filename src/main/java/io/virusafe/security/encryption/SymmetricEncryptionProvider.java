package io.virusafe.security.encryption;

/**
 * Symmetric encryption provider
 */
public interface SymmetricEncryptionProvider {
    /**
     * Encrypt data
     *
     * @param data
     * @param iv
     * @return
     */
    String encrypt(String data, String iv);

    /**
     * Decrypt data
     *
     * @param data
     * @param iv
     * @return
     */
    String decrypt(String data, String iv);
}
