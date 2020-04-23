package io.virusafe.sms;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * HmacSHA512 hash converted to string in a way supported by sms provider
 */
public class HmacHashGenerator implements HashGenerator {
    private static final String ENCRYPTION_ALGORITHM = "HmacSHA512";
    private final String apiSecret;

    /**
     * @param apiSecret - secret key provided by SMS provider
     */
    public HmacHashGenerator(final String apiSecret) {
        this.apiSecret = apiSecret;
    }

    @Override
    public String generateHash(final String message) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac sha512HMAC = Mac.getInstance(ENCRYPTION_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), ENCRYPTION_ALGORITHM);
        sha512HMAC.init(secretKey);

        return toHexString(sha512HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    private static String toHexString(final byte[] bytes) {
        try (Formatter formatter = new Formatter();) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
