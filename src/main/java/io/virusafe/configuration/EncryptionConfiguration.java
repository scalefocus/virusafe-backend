package io.virusafe.configuration;

import io.virusafe.repository.EncryptionUserDetailsRepositoryFacade;
import io.virusafe.repository.NoEncryptionUserDetailsRepositoryFacade;
import io.virusafe.repository.UserDetailsRepository;
import io.virusafe.repository.UserDetailsRepositoryFacade;
import io.virusafe.security.encryption.SymmetricEncryptionProvider;
import io.virusafe.security.encryption.SymmetricEncryptionProviderImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfiguration {

    /**
     * Provide a SymmetricEncryptionProvider bean with name identificationNumber,
     * registered for encryption of personal data. Uses a provided encryption key and configured algorithms.
     *
     * @param encryptionKey                     the symmetric key to use for encryption
     * @param encryptionConfigurationProperties the autowired algorithm configuration properties
     * @return the personal data SymmetricEncryptionProvider
     */
    @Bean("identificationNumber")
    public SymmetricEncryptionProvider createEncryptionProvider(
            @Value("${encryption.symmetric.key}") final String encryptionKey,
            final EncryptionAlgorithmProperties encryptionConfigurationProperties) {
        return new SymmetricEncryptionProviderImpl(
                encryptionKey,
                encryptionConfigurationProperties.getDigest(),
                encryptionConfigurationProperties.getEncryption(),
                encryptionConfigurationProperties.getKey());
    }

    /**
     * Provide a SymmetricEncryptionProvider bean with name registerEncryption,
     * meant for encryption of the complete messages we send to the central register.
     * Uses a provided encryption key and configured algorithms.
     *
     * @param encryptionKey                     the symmetric key to use for encryption
     * @param encryptionConfigurationProperties the autowired algorithm configuration properties
     * @return the register SymmetricEncryptionProvider
     */
    @Bean("registerEncryption")
    public SymmetricEncryptionProvider createRegisterEncryptionProvider(
            @Value("${register.integration.kafka.symmetric.key}") final String encryptionKey,
            final EncryptionAlgorithmProperties encryptionConfigurationProperties) {
        return new SymmetricEncryptionProviderImpl(
                encryptionKey,
                encryptionConfigurationProperties.getDigest(),
                encryptionConfigurationProperties.getEncryption(),
                encryptionConfigurationProperties.getKey());
    }

    /**
     * Provide an encrypting UserDetailsRepositoryFacade bean to encrypt/decrypt personal information data
     * when communicating with the UserDetailsRepository.
     * Usage of this bean is configurable by the encryption.provider.enable property.
     *
     * @param symmetricEncryptionProvider  the autowired identificationNumber-named SymmetricEncryptionProvider bean to use
     * @param userDetailsRepository        the autowired UserDetailsRepository to communicate with
     * @param identificationNumberIVVector the initialization vector to use for encryption
     * @return the encrypting UserDetailsRepositoryFacade
     */
    @Bean
    @ConditionalOnProperty(value = "encryption.provider.enable", havingValue = "true")
    public UserDetailsRepositoryFacade createEncryptionRepositoryFacade(
            @Qualifier("identificationNumber") final SymmetricEncryptionProvider symmetricEncryptionProvider,
            final UserDetailsRepository userDetailsRepository,
            @Value("${encryption.iv.identification.number}") final String identificationNumberIVVector) {
        return new EncryptionUserDetailsRepositoryFacade(symmetricEncryptionProvider, userDetailsRepository,
                identificationNumberIVVector);
    }

    /**
     * Provide a non-encrypting UserDetailsRepositoryFacade bean for straight communication with the UserDetailsRepository.
     * Usage of this bean is configurable by the encryption.provider.enable property.
     *
     * @param userDetailsRepository the autowired UserDetailsRepository to communicate with
     * @return the non-encrypting UserDetailsRepositoryFacade
     */
    @Bean
    @ConditionalOnProperty(value = "encryption.provider.enable", havingValue = "false", matchIfMissing = true)
    public UserDetailsRepositoryFacade createNoEncryptionRepositoryFacade(
            final UserDetailsRepository userDetailsRepository) {
        return new NoEncryptionUserDetailsRepositoryFacade(userDetailsRepository);
    }

    @Configuration
    @ConfigurationProperties(prefix = "encryption.algorithm")
    @Getter
    @Setter
    public static class EncryptionAlgorithmProperties {
        private String digest;
        private String encryption;
        private String key;
    }
}