package io.virusafe.configuration;

import io.virusafe.repository.NoEncryptionUserDetailsRepositoryFacade;
import io.virusafe.repository.UserDetailsRepository;
import io.virusafe.repository.UserDetailsRepositoryFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EncryptionConfigurationTest {

    @Mock
    private UserDetailsRepository userDetailsRepository;
    private final EncryptionConfiguration encryptionConfiguration = new EncryptionConfiguration();

    @Test
    public void testCanCreateNoEncryptionProviderFacade() {
        UserDetailsRepositoryFacade repositoryFacade =
                encryptionConfiguration.createNoEncryptionRepositoryFacade(userDetailsRepository);
        assertTrue(repositoryFacade instanceof NoEncryptionUserDetailsRepositoryFacade);
    }
}