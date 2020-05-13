package io.virusafe.repository;

import io.virusafe.domain.entity.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface UserDetailsRepositoryFacade {

    /**
     * Find UserDetails by user GUID, returning empty Optional if not found.
     *
     * @param userGuid the user GUID to search for
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    Optional<UserDetails> findByUserGuid(String userGuid);

    /**
     * Find UserDetails by phone number, returning empty Optional if not found.
     *
     * @param phoneNumber the phone number to search for
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    Optional<UserDetails> findByPhoneNumber(String phoneNumber);

    /**
     * Find UserDetails by phone number, registration PIN and PIN validity check time.
     * Only returns UserDetails if the registration PIN is valid until after the provided validity check time.
     *
     * @param phoneNumber the phone number to search for
     * @param pin         the PIN to search for
     * @param time        the validity time to check registration PINs against
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    Optional<UserDetails> findByPhoneNumberAndValidPin(String phoneNumber, String pin, LocalDateTime time);

    /**
     * Find UserDetails by refresh token, returning empty Optional if not found.
     *
     * @param refreshTokenHash the refresh token to search for
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    Optional<UserDetails> findByRefreshToken(String refreshTokenHash);

    /**
     * Save the provided UserDetails.
     *
     * @param userDetails the UserDetails to save
     */
    void save(UserDetails userDetails);

    /**
     * Find push tokens by userGuids.
     *
     * @param userGuids userGuids to search for
     */
    Set<String> findAllPushTokensByUserGuid(Set<String> userGuids, final boolean reverse);
}
