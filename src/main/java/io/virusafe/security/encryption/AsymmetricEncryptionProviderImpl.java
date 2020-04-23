package io.virusafe.security.encryption;

import io.virusafe.exception.EncryptionProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * Default asymmetric encryption provider
 */
public class AsymmetricEncryptionProviderImpl implements AsymmetricEncryptionProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String algorithm;

    /**
     * Construct asymmetric encryption provider
     *
     * @param privateKeyData
     * @param publicKeyData
     * @param algorithm
     */
    public AsymmetricEncryptionProviderImpl(final String privateKeyData, final String publicKeyData,
                                            final String algorithm) {
        privateKey = readPrivateRsaKey(privateKeyData, algorithm);
        publicKey = readPublicRsaKey(publicKeyData, algorithm);
        this.algorithm = algorithm;
    }

    @Override
    public String encrypt(final String data) {
        return encrypt(data, privateKey);
    }

    @Override
    public String decrypt(final String data) {
        return decrypt(data, publicKey);
    }

    private synchronized String encrypt(final String plainText, final PrivateKey privateKey) {
        try {
            Objects.requireNonNull(plainText);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encStr = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encStr);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new EncryptionProviderException("Cannot encrypt data with RSA ", e);
        }
    }

    private synchronized String decrypt(final String encData, final PublicKey publicKey) {
        try {
            Objects.requireNonNull(encData);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decStr = cipher.doFinal(Base64.getDecoder().decode(encData));
            return new String(decStr, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new EncryptionProviderException("Cannot decrypt data with RSA ", e);
        }
    }

    private static PublicKey readPublicRsaKey(final String publicKey, final String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionProviderException("Unable to unwrap public key.", e);
        }
    }

    private static PrivateKey readPrivateRsaKey(final String privateKey, final String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionProviderException("Unable to unwrap public key.", e);
        }
    }
}
