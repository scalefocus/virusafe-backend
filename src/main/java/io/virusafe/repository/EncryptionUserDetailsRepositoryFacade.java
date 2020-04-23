package io.virusafe.repository;

import io.virusafe.domain.entity.UserDetails;
import io.virusafe.security.encryption.SymmetricEncryptionProvider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class EncryptionUserDetailsRepositoryFacade implements UserDetailsRepositoryFacade {

    private final SymmetricEncryptionProvider symmetricEncryptionProvider;
    private final UserDetailsRepository userDetailsRepository;
    private final String identificationNumberIVVector;

    /**
     * Construct a new EncryptionUserDetailsRepositoryFacade, using the provided SymmetricEncryptionProvider to
     * handle encryption, UserDetailsRepository to communicate with the data layer and encryption initialization vector.
     * This implementation of UserDetailsRepositoryFacade encrypts identificationNumber before saving it
     * and decrypts it when fetching it from the DB.
     *
     * @param symmetricEncryptionProvider  the SymmetricEncryptionProvider to use
     * @param userDetailsRepository        the UserDetailsRepository to use
     * @param identificationNumberIVVector the initialization vector for identification number
     */
    public EncryptionUserDetailsRepositoryFacade(
            final SymmetricEncryptionProvider symmetricEncryptionProvider,
            final UserDetailsRepository userDetailsRepository,
            final String identificationNumberIVVector) {
        this.symmetricEncryptionProvider = symmetricEncryptionProvider;
        this.userDetailsRepository = userDetailsRepository;
        this.identificationNumberIVVector = identificationNumberIVVector;
    }

    @Override
    public Optional<UserDetails> findByUserGuid(final String userGuid) {
        return decodeDetails(userDetailsRepository.findByUserGuid(userGuid));
    }

    @Override
    public Optional<UserDetails> findByPhoneNumber(final String phoneNumber) {
        return decodeDetails(userDetailsRepository.findByPhoneNumber(phoneNumber));
    }

    @Override
    public Optional<UserDetails> findByPhoneNumberAndValidPin(final String phoneNumber,
                                                              final String pin,
                                                              final LocalDateTime time) {
        return decodeDetails(userDetailsRepository
                .findByPhoneNumberAndValidPin(phoneNumber, pin, time));
    }

    @Override
    public Optional<UserDetails> findByRefreshToken(final String refreshTokenHash) {
        return decodeDetails(userDetailsRepository.findByRefreshToken(refreshTokenHash));
    }

    private Optional<UserDetails> decodeDetails(final Optional<UserDetails> userDetailsOpt) {
        if (userDetailsOpt.isPresent()) {
            UserDetails userDetails = userDetailsOpt.get();
            try {
                return decode(userDetails, symmetricEncryptionProvider);
            } catch (Exception e) {
                log.error("Cannot decode data for user {}", userDetails.getUserGuid());
                userDetails.setIdentificationNumberPlain(null);
            }
        }
        return userDetailsOpt;
    }

    @Override
    public void save(final UserDetails userDetails) {
        userDetailsRepository.save(encode(userDetails));
    }

    private Optional<UserDetails> decode(final UserDetails userDetails,
                                         final SymmetricEncryptionProvider encryptionProvider) {
        if (Objects.nonNull(userDetails.getIdentificationNumber())) {
            userDetails.setIdentificationNumberPlain(
                    encryptionProvider.decrypt(userDetails.getIdentificationNumber(),
                            identificationNumberIVVector));
        } else {
            userDetails.setIdentificationNumberPlain(null);
        }
        return Optional.of(userDetails);
    }

    private UserDetails encode(final UserDetails userDetails) {
        if (Objects.nonNull(userDetails.getIdentificationNumberPlain())) {
            userDetails.setIdentificationNumber(
                    symmetricEncryptionProvider.encrypt(userDetails.getIdentificationNumberPlain(),
                            identificationNumberIVVector));
        } else {
            userDetails.setIdentificationNumber(null);
        }
        return userDetails;
    }

    @Override
    public Set<String> findAllPushTokensByUserGuid(final Set<String> userGuids) {
        return userDetailsRepository.findAllPushTokensByUserGuid(userGuids);
    }
}
