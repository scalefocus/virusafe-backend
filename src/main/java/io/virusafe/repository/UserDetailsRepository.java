package io.virusafe.repository;

import io.virusafe.domain.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

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
     * @param phoneNumber  the phone number to search for
     * @param pin          the PIN to search for
     * @param validityTime the validity time to check registration PINs against
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    @Query("SELECT ud FROM UserDetails ud " +
            "INNER JOIN FETCH ud.registrationPins registrationPin " +
            "WHERE ud.phoneNumber = :phoneNumber " +
            "AND registrationPin.pin = :pin " +
            "AND registrationPin.validUntil >= :validityTime")
    Optional<UserDetails> findByPhoneNumberAndValidPin(@Param("phoneNumber") String phoneNumber,
                                                       @Param("pin") String pin,
                                                       @Param("validityTime") LocalDateTime validityTime);

    /**
     * Find UserDetails by refresh token, returning empty Optional if not found.
     *
     * @param refreshTokenHash the refresh token to search for
     * @return Optional containing either the found UserDetails or empty if nothing was found
     */
    Optional<UserDetails> findByRefreshToken(String refreshTokenHash);

    /**
     * Find push tokens by userGuids.
     *
     * @param userGuids userGuids to search for
     */
    @Query("SELECT ud.pushToken FROM UserDetails ud WHERE ud.userGuid IN (:userGuids)")
    Set<String> findAllPushTokensByUserGuid(@Param("userGuids") Set<String> userGuids);
}
