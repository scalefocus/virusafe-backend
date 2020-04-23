package io.virusafe.repository;

import io.virusafe.domain.entity.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class NoEncryptionUserDetailsRepositoryFacade implements UserDetailsRepositoryFacade {

    private final UserDetailsRepository userDetailsRepository;

    /**
     * Construct a new NoEncryptionUserDetailsRepositoryFacade, using the provided UserDetailsRepository
     * for communication with the data layer.
     * This implementation of UserDetailsRepositoryFacade does not do any encryption on top of the UserDetails.
     *
     * @param userDetailsRepository the UserDetailsRepository to use
     */
    @Autowired
    public NoEncryptionUserDetailsRepositoryFacade(final UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
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
            return decode(userDetailsOpt.get());
        }
        return userDetailsOpt;
    }

    @Override
    public void save(final UserDetails userDetails) {
        userDetailsRepository.save(encode(userDetails));
    }

    private Optional<UserDetails> decode(final UserDetails userDetails) {
        userDetails.setIdentificationNumberPlain(userDetails.getIdentificationNumber());
        return Optional.of(userDetails);
    }

    private UserDetails encode(final UserDetails userDetails) {
        userDetails.setIdentificationNumber(userDetails.getIdentificationNumberPlain());
        return userDetails;
    }

    @Override
    public Set<String> findAllPushTokensByUserGuid(final Set<String> userGuids) {
        return userDetailsRepository.findAllPushTokensByUserGuid(userGuids);
    }
}
