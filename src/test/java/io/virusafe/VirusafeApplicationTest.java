package io.virusafe;

import io.virusafe.configuration.MockElasticConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test"})
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {12321})
class VirusafeApplicationTest {

    public VirusafeApplicationTest(final MockServerClient client) {
        new MockElasticConfiguration().setup(client);
    }

    @Test
    void contextLoads() {
    }
}
