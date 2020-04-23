package io.virusafe.security.encryption;

import io.virusafe.exception.EncryptionProviderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AsymmetricEncryptionProviderImplTest {
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApiuI8YWJjekbDG2k8XX8vv2nlqqyhK+Esf6BUIs4enGdahgfmXGVqoTt+Z0lWqUlrVzLFtz6SBh2hkcVC8JZ1OE48BkZDGpu3AkBV8iaBZys6H+uUflc0i+5jjEuqv6NE7fKb1N2htHu4MmwpHkMMmILSwa7+WHi8nAMCdNTBptpmVFm3GcRWiyXe2ebcW1mSmNfF/0YLoMjZn7AYZThv5DRn/fY3/rhE524wss9Piko7ffagdRUnW3duvJgg2RkOp4jK48k0n9msgEzJyW+UiVdeI7adD6WPLfZhj5wONtvJ9j1bIE2PqnLO6jV6+dA6lnJ4M26MP5meSBi2gqW/wIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCmK4jxhYmN6RsMbaTxdfy+/aeWqrKEr4Sx/oFQizh6cZ1qGB+ZcZWqhO35nSVapSWtXMsW3PpIGHaGRxULwlnU4TjwGRkMam7cCQFXyJoFnKzof65R+VzSL7mOMS6q/o0Tt8pvU3aG0e7gybCkeQwyYgtLBrv5YeLycAwJ01MGm2mZUWbcZxFaLJd7Z5txbWZKY18X/RgugyNmfsBhlOG/kNGf99jf+uETnbjCyz0+KSjt99qB1FSdbd268mCDZGQ6niMrjyTSf2ayATMnJb5SJV14jtp0PpY8t9mGPnA4228n2PVsgTY+qcs7qNXr50DqWcngzbow/mZ5IGLaCpb/AgMBAAECggEAPd0xY7Gyj2m8jzj20op5qWVoEjjEs49g2J+G50Sdp0BLOad3hDn8XXqV2nt936xiRZOpbH6ik1rALaejFzipFw8uknzVjYDGexbDMDpX1UUKPpjiflRXEU2BSIXY+QJB5ZhkPmQVWYYo5/lO0TzbpJpJ2AnOy758roO2h6XsYCW6MysLhFDpdYDAw4SoPpphHXmHj9V/Dsu1pGXuBWmsPa6CzM/SBWsVwUAz88Es0+1UQFApajf85jwLF6qti6z/y33P7jF5MqL/6M0opUV7jrGT+09LAr126dZvz6eC5e7C78XQA+PCJXfallal55A2m9aG84CQq3JNjVDKWK81mQKBgQD0eWW5T6XIpp5fs/Wt0z6WEIbRQJ4e27OS7AL03A+9ZuT23lIhTOsS4qBHf2eTpp1FrIJNOdIwXGLl8feZR87pJGSn0k1mnNOCGjJOws50zYf3Szm/wtv10ofDfjQ4KyDCoI2pGgIYBlJfjPy1Gkjf9YHhDC9xRoy2bJx7qK9jtQKBgQCuARIlUJvpu1la74q7/KX8tEHV0G9dKh0mW98pnMDLQMsohX/U1bURGMjtOrtcZkikILAHJ7LUX9aQ9tfzyiAsNigSHzsDbP6z0RL2CQ6kCF1I1NvqZZWZ/Yl+ewq6LyPZdgwrKNAznzJkmRkOVvv1CGOVlbjxnBv/ZSkYq0/oYwKBgQDKXNJ/Gha4iHL/9Q6f3AQqY3O1Mi+wi1uRehrYZHAC73EHh++j8O+EqmLRy/wIlBoK2kjhc8MwlMioXin9jxsOkTlgUQwQrC/Hubd6ynXq6VZqtLIQsVMxoBsRDx5agBiWAiBAoW6qRFFbFnD319IK0EW560Q6F5PSTQUatv63IQKBgGlvTq57pK3BDwjSAy+bjHyOQSIs9X3vSdB3dMbDK/M85J0+H1I42EluyjktEGCRvcxt3hvHy01ycRJP8FTRkiMYAbnRYLHXtpIAyst4e32bb+L0z/i86yRvA4Q3TC266K8ZH5B7X4UYHrHkLtGIlY1JMwZtva3xGOntQzSmgXnfAoGAVlhdbqdwlOsOHiSmy5RRBgKXgP1MHlQH7TYqfkzIjyqbFsm7fdRVbq2l5tqYPjUGyJ2qr4QvEcU/N8V+1HIbjrx6mpzISditgH3RJDEvtu01ljxWWfL5x0hhsPAx7qOIGeatv6R/3D99kqk83J5WOH/1Us041NKk7QQB6Wjaajc=";
    private static final String TEST_PLAIN_TEXT = "testPlainText";
    private static final String ALGORITHM = "RSA";
    private static final String INVALID_ALGORITHM = "INVALID_ALGORITHM";
    private static final String ALGORITHM_FIELD_NAME = "algorithm";
    private static final String READ_PUBLIC_RSA_KEY_METHOD_NAME = "readPublicRsaKey";

    @Test
    void testEncryptDecrypt() {
        AsymmetricEncryptionProvider asymmetricEncryptionProvider = new AsymmetricEncryptionProviderImpl(PRIVATE_KEY,
                PUBLIC_KEY, ALGORITHM);
        String encryptedText = asymmetricEncryptionProvider.encrypt(TEST_PLAIN_TEXT);
        String decryptedText = asymmetricEncryptionProvider.decrypt(encryptedText);
        Assertions.assertEquals(TEST_PLAIN_TEXT, decryptedText);
    }

    @Test
    void testConstructorThrowsExceptionForInvalidAlgorithm() {
        AsymmetricEncryptionProvider asymmetricEncryptionProvider = new AsymmetricEncryptionProviderImpl(PRIVATE_KEY,
                PUBLIC_KEY, ALGORITHM);
        assertThrows(EncryptionProviderException.class, () ->
                ReflectionTestUtils.invokeMethod(asymmetricEncryptionProvider, READ_PUBLIC_RSA_KEY_METHOD_NAME,
                        PUBLIC_KEY, INVALID_ALGORITHM));
    }

    @Test
    void testReadPublicKeyThrowsExceptionForInvalidAlgorithm() {
        assertThrows(EncryptionProviderException.class, () ->
                new AsymmetricEncryptionProviderImpl(PRIVATE_KEY, PUBLIC_KEY, INVALID_ALGORITHM));
    }

    @Test
    void testEncryptFailsForInvalidAlgorithm() {
        AsymmetricEncryptionProvider asymmetricEncryptionProvider = new AsymmetricEncryptionProviderImpl(PRIVATE_KEY,
                PUBLIC_KEY, ALGORITHM);
        ReflectionTestUtils.setField(asymmetricEncryptionProvider, ALGORITHM_FIELD_NAME, INVALID_ALGORITHM);
        assertThrows(EncryptionProviderException.class, () ->
                asymmetricEncryptionProvider.encrypt(TEST_PLAIN_TEXT));
    }

    @Test
    void testDecryptFailsForInvalidAlgorithm() {
        AsymmetricEncryptionProvider asymmetricEncryptionProvider = new AsymmetricEncryptionProviderImpl(PRIVATE_KEY,
                PUBLIC_KEY, ALGORITHM);
        String encryptedText = asymmetricEncryptionProvider.encrypt(TEST_PLAIN_TEXT);
        ReflectionTestUtils.setField(asymmetricEncryptionProvider, ALGORITHM_FIELD_NAME, INVALID_ALGORITHM);
        assertThrows(EncryptionProviderException.class, () ->
                asymmetricEncryptionProvider.decrypt(encryptedText));
    }

}