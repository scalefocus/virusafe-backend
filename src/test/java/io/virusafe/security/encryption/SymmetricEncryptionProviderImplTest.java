package io.virusafe.security.encryption;

import io.virusafe.exception.EncryptionProviderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SymmetricEncryptionProviderImplTest {
    private static final String SYMMETRIC_KEY = "tofx_QFx9Ltpb-vbfr3BXm0wiHNVTPF8g8cCnqqmrw50hWNN76iSEvXvYiw0H9D2hQGodABqQX5LCuP9WLnXeQSwaHqFoYwAgEuLDr-D7LqvSJ0UFTqzeFJgOpzecSYW6Gvmq-4ADfbwsB8TCNQVnbvvgJcasNd7iCthFRjNdjbVJzig_V_NrsVOgo7y0c3jkwa_RGMkI3y0fF5zFlkY3LM8xbIEoJ3qBLSDCHZtbiSKGkLjakL1ZK9YTNp5Rb17Ar-FxS4zSybqW8KCHLaxIvHy85V4ojwj7qx8hKTOms9hqCila-fIS1IBC-RYMZmkhe2Lld_0xRAcMYPSSdBiFQ";
    private static final String IV_EGN = "EGN";
    private static final String IV_PHONE_NUMBER = "PhoneNumber";
    private static final String TEST_EGN = "9876543210";
    private static final String TEST_EGN_2 = "0123456789";
    private static final String TEST_PHONE = "0889123654";
    private static final String TEST_PHONE_2 = "0878987456";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String INVALID_ALGORITHM = "INVALID_ALGORITHM";

    @Test
    public void testTestWithEGN() {
        SymmetricEncryptionProvider encryptionProvider = new SymmetricEncryptionProviderImpl(SYMMETRIC_KEY, DIGEST_ALGORITHM,
                ENCRYPTION_ALGORITHM, KEY_ALGORITHM);
        String encString = encryptionProvider.encrypt(TEST_EGN, IV_EGN);
        String encString2 = encryptionProvider.encrypt(TEST_EGN_2, IV_EGN);
        String decString = encryptionProvider.decrypt(encString, IV_EGN);
        String decString2 = encryptionProvider.decrypt(encString2, IV_EGN);
        Assertions.assertEquals(TEST_EGN, decString);
        Assertions.assertEquals(TEST_EGN_2, decString2);
    }

    @Test
    public void testTestWithPhoneNumber() {
        SymmetricEncryptionProviderImpl encryptionProvider = new SymmetricEncryptionProviderImpl(SYMMETRIC_KEY, DIGEST_ALGORITHM,
                ENCRYPTION_ALGORITHM, KEY_ALGORITHM);
        String encString = encryptionProvider.encrypt(TEST_PHONE, IV_PHONE_NUMBER);
        String encString2 = encryptionProvider.encrypt(TEST_PHONE_2, IV_PHONE_NUMBER);
        String decString = encryptionProvider.decrypt(encString, IV_PHONE_NUMBER);
        String decString2 = encryptionProvider.decrypt(encString2, IV_PHONE_NUMBER);
        Assertions.assertEquals(TEST_PHONE, decString);
        Assertions.assertEquals(TEST_PHONE_2, decString2);
    }

    @Test
    public void testEncryptionProviderExceptionIsThrownForInvalidDigestAlgorithm() {
        assertThrows(EncryptionProviderException.class, () ->
                new SymmetricEncryptionProviderImpl(SYMMETRIC_KEY, INVALID_ALGORITHM, ENCRYPTION_ALGORITHM, KEY_ALGORITHM));
    }

    @Test
    public void testEncryptionFailsWithInvalidAlgorithm() {
        SymmetricEncryptionProviderImpl encryptionProvider = new SymmetricEncryptionProviderImpl(SYMMETRIC_KEY, DIGEST_ALGORITHM,
                INVALID_ALGORITHM, KEY_ALGORITHM);
        assertThrows(EncryptionProviderException.class, () ->
                encryptionProvider.encrypt(TEST_PHONE, IV_PHONE_NUMBER));
    }

    @Test
    public void testDecryptionFailsWithInvalidAlgorithm() {
        SymmetricEncryptionProviderImpl encryptionProvider = new SymmetricEncryptionProviderImpl(SYMMETRIC_KEY, DIGEST_ALGORITHM,
                INVALID_ALGORITHM, KEY_ALGORITHM);
        assertThrows(EncryptionProviderException.class, () ->
                encryptionProvider.decrypt(TEST_PHONE, IV_PHONE_NUMBER));
    }
}
