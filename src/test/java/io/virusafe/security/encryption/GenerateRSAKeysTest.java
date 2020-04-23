package io.virusafe.security.encryption;

import io.virusafe.exception.EncryptionProviderException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class GenerateRSAKeysTest {

    private static final String RSA_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApiuI8YWJjekbDG2k8XX8vv2nlqqyhK+Esf6BUIs4enGdahgfmXGVqoTt+Z0lWqUlrVzLFtz6SBh2hkcVC8JZ1OE48BkZDGpu3AkBV8iaBZys6H+uUflc0i+5jjEuqv6NE7fKb1N2htHu4MmwpHkMMmILSwa7+WHi8nAMCdNTBptpmVFm3GcRWiyXe2ebcW1mSmNfF/0YLoMjZn7AYZThv5DRn/fY3/rhE524wss9Piko7ffagdRUnW3duvJgg2RkOp4jK48k0n9msgEzJyW+UiVdeI7adD6WPLfZhj5wONtvJ9j1bIE2PqnLO6jV6+dA6lnJ4M26MP5meSBi2gqW/wIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCmK4jxhYmN6RsMbaTxdfy+/aeWqrKEr4Sx/oFQizh6cZ1qGB+ZcZWqhO35nSVapSWtXMsW3PpIGHaGRxULwlnU4TjwGRkMam7cCQFXyJoFnKzof65R+VzSL7mOMS6q/o0Tt8pvU3aG0e7gybCkeQwyYgtLBrv5YeLycAwJ01MGm2mZUWbcZxFaLJd7Z5txbWZKY18X/RgugyNmfsBhlOG/kNGf99jf+uETnbjCyz0+KSjt99qB1FSdbd268mCDZGQ6niMrjyTSf2ayATMnJb5SJV14jtp0PpY8t9mGPnA4228n2PVsgTY+qcs7qNXr50DqWcngzbow/mZ5IGLaCpb/AgMBAAECggEAPd0xY7Gyj2m8jzj20op5qWVoEjjEs49g2J+G50Sdp0BLOad3hDn8XXqV2nt936xiRZOpbH6ik1rALaejFzipFw8uknzVjYDGexbDMDpX1UUKPpjiflRXEU2BSIXY+QJB5ZhkPmQVWYYo5/lO0TzbpJpJ2AnOy758roO2h6XsYCW6MysLhFDpdYDAw4SoPpphHXmHj9V/Dsu1pGXuBWmsPa6CzM/SBWsVwUAz88Es0+1UQFApajf85jwLF6qti6z/y33P7jF5MqL/6M0opUV7jrGT+09LAr126dZvz6eC5e7C78XQA+PCJXfallal55A2m9aG84CQq3JNjVDKWK81mQKBgQD0eWW5T6XIpp5fs/Wt0z6WEIbRQJ4e27OS7AL03A+9ZuT23lIhTOsS4qBHf2eTpp1FrIJNOdIwXGLl8feZR87pJGSn0k1mnNOCGjJOws50zYf3Szm/wtv10ofDfjQ4KyDCoI2pGgIYBlJfjPy1Gkjf9YHhDC9xRoy2bJx7qK9jtQKBgQCuARIlUJvpu1la74q7/KX8tEHV0G9dKh0mW98pnMDLQMsohX/U1bURGMjtOrtcZkikILAHJ7LUX9aQ9tfzyiAsNigSHzsDbP6z0RL2CQ6kCF1I1NvqZZWZ/Yl+ewq6LyPZdgwrKNAznzJkmRkOVvv1CGOVlbjxnBv/ZSkYq0/oYwKBgQDKXNJ/Gha4iHL/9Q6f3AQqY3O1Mi+wi1uRehrYZHAC73EHh++j8O+EqmLRy/wIlBoK2kjhc8MwlMioXin9jxsOkTlgUQwQrC/Hubd6ynXq6VZqtLIQsVMxoBsRDx5agBiWAiBAoW6qRFFbFnD319IK0EW560Q6F5PSTQUatv63IQKBgGlvTq57pK3BDwjSAy+bjHyOQSIs9X3vSdB3dMbDK/M85J0+H1I42EluyjktEGCRvcxt3hvHy01ycRJP8FTRkiMYAbnRYLHXtpIAyst4e32bb+L0z/i86yRvA4Q3TC266K8ZH5B7X4UYHrHkLtGIlY1JMwZtva3xGOntQzSmgXnfAoGAVlhdbqdwlOsOHiSmy5RRBgKXgP1MHlQH7TYqfkzIjyqbFsm7fdRVbq2l5tqYPjUGyJ2qr4QvEcU/N8V+1HIbjrx6mpzISditgH3RJDEvtu01ljxWWfL5x0hhsPAx7qOIGeatv6R/3D99kqk83J5WOH/1Us041NKk7QQB6Wjaajc=";
    private static final String TEST_PLAIN_TEXT = "testPlainText";

    @Test
    void testGenerateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(KEY_SIZE);

        KeyPair pair = keyGen.generateKeyPair();
        Assertions.assertNotNull(pair);
        log.info("Generated Public key Base64: " + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        log.info("Generated Private key Base64: " + Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
    }

    @Test
    void testEncryptDecrypt() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        KeyPair pair = keyGen.generateKeyPair();

        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pair.getPrivate());
        byte[] encStr = cipher.doFinal(TEST_PLAIN_TEXT.getBytes(StandardCharsets.UTF_8));
        System.out.println(Base64.getEncoder().encodeToString(encStr));

        Cipher cipher1 = Cipher.getInstance(RSA_ALGORITHM);
        cipher1.init(Cipher.DECRYPT_MODE, pair.getPublic());
        byte[] decStr = cipher1.doFinal(encStr);
        System.out.println(new String(decStr, StandardCharsets.UTF_8));

        Assertions.assertEquals(TEST_PLAIN_TEXT, new String(decStr, StandardCharsets.UTF_8));
    }

    @Test
    void testEncryptDecryptUsingOurMethods() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        KeyPair pair = keyGen.generateKeyPair();

        String encryptedData = encrypt(TEST_PLAIN_TEXT, pair.getPrivate());
        String decryptedData = decrypt(encryptedData, pair.getPublic());

        Assertions.assertEquals(TEST_PLAIN_TEXT, decryptedData);
    }

    @Test
    void testEncryptDecryptUsingOurMethodsAndKeys() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String encryptedData = encrypt(TEST_PLAIN_TEXT, readPrivateRsaKey(PRIVATE_KEY));
        String decryptedData = decrypt(encryptedData, readPublicRsaKey(PUBLIC_KEY));

        Assertions.assertEquals(TEST_PLAIN_TEXT, decryptedData);
    }

    private synchronized String encrypt(String plainText,
                                        PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(plainText);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encStr = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encStr);
    }

    private synchronized String decrypt(String encData,
                                        PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Objects.requireNonNull(encData);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decStr = cipher.doFinal(Base64.getDecoder().decode(encData));
        return new String(decStr, StandardCharsets.UTF_8);
    }

    public static PublicKey readPublicRsaKey(String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionProviderException("Unable to unwrap public key.", e);
        }
    }

    public static PrivateKey readPrivateRsaKey(String privateKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionProviderException("Unable to unwrap public key.", e);
        }
    }
}
