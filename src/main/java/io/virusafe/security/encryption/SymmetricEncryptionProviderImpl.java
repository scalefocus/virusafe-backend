package io.virusafe.security.encryption;

import io.virusafe.exception.EncryptionProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Default symmetric encryption provider
 */
public class SymmetricEncryptionProviderImpl implements SymmetricEncryptionProvider {
    private static final int IV_KEY_LENGTH = 16;

    //we use it only in synchronized environment
    @SuppressWarnings("PMD.AvoidMessageDigestField")
    private final MessageDigest digest;
    private final byte[] encKey;
    private final String encryptionAlgorithm;
    private final String keyAlgorithm;

    /**
     * Construct symmetric encryption provider
     *
     * @param key
     * @param digestAlgorithm
     * @param encryptionAlgorithm
     * @param keyAlgorithm
     */
    public SymmetricEncryptionProviderImpl(final String key, final String digestAlgorithm,
                                           final String encryptionAlgorithm, final String keyAlgorithm) {
        this.digest = getMessageDigest(digestAlgorithm);
        this.encKey = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.keyAlgorithm = keyAlgorithm;
    }

    private MessageDigest getMessageDigest(final String digestAlgorithm) {
        try {
            return MessageDigest.getInstance(digestAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionProviderException("Can't find encryption provider", e);
        }
    }

    @Override
    public synchronized String encrypt(final String data, final String iv) {
        try {
            final Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encKey, keyAlgorithm),
                    new IvParameterSpec(getFirst16Bytes(iv)));

            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionProviderException("Cannot Encode text", e);
        }
    }

    @Override
    public synchronized String decrypt(final String data, final String iv) {
        try {
            final Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encKey, keyAlgorithm),
                    new IvParameterSpec(getFirst16Bytes(iv)));
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionProviderException("Cannot Decode text", e);
        }
    }

    private byte[] getFirst16Bytes(final String iv) {
        byte[] digestKey = digest.digest(iv.getBytes(StandardCharsets.UTF_8));
        return Arrays.copyOf(digestKey, IV_KEY_LENGTH);
    }
}