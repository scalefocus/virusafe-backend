package io.virusafe.sms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class HmacHashGeneratorTest {

    private static final String TEST_MESSAGE = "TestMessage";
    private static final String API_SECRET = "API_SECRET";
    private static final String EXPECTED_HASH = "9b886a08ed2bfe299f61233fbe8b6c5b5c1d40f456f2633d4b2e5bd84f03af34490f09b0f026984a7cd5b205dd8139399fa3837d6170069e76fd11ecc9493efa";

    @Test
    void generateHashApiKeyNpe() {
        HashGenerator hashGenerator = new HmacHashGenerator(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            hashGenerator.generateHash(TEST_MESSAGE);
        });
    }

    @Test
    void generateHmacHash() throws InvalidKeyException, NoSuchAlgorithmException {
        HashGenerator hashGenerator = new HmacHashGenerator(API_SECRET);

        String hmacHash = hashGenerator.generateHash(TEST_MESSAGE);

        Assertions.assertEquals(EXPECTED_HASH, hmacHash);

    }
}