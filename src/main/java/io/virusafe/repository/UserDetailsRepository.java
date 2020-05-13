package io.virusafe.repository;

import io.virusafe.domain.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long>, JpaSpecificationExecutor<UserDetails> {

    String USER_GUIDS = "userGuids";
    String GRANTED_CHECK = " and 'GRANTED' = (select pica.action from personal_information_consent_audit pica " +
            "                   where pica.user_guid=ud.user_guid " +
            "                   ORDER BY pica.changed_on DESC " +
            "                       LIMIT 1)";

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
     * @param notIn     if true it will invert logic
     */
    default Set<String> findAllPushTokensByUserGuids(final Set<String> userGuids, final boolean notIn) {
        if (!notIn) {
            return findAllPushTokensByUserGuidIn(userGuids);
        }
        if (!userGuids.isEmpty()) {
            return findAllPushTokensByUserGuidNotIn(userGuids);
        }
        return findAllPushTokens();
    }

    /**
     * Find push tokens by userGuids.
     *
     * @param userGuids userGuids to search for
     */
    @Query(value = "select ud.push_token from user_details ud where ud.push_token is not null " +
            " and ud.user_guid in (:userGuids) " + GRANTED_CHECK, nativeQuery = true)
    Set<String> findAllPushTokensByUserGuidIn(@Param(USER_GUIDS) Set<String> userGuids);

    /**
     * Find reversed results for push tokens by userGuids.
     *
     * @param userGuids userGuids to search for
     */
    @Query(value = "select ud.push_token from user_details ud where ud.push_token is not null " +
            " and ud.user_guid not in (:userGuids) " + GRANTED_CHECK, nativeQuery = true)
    Set<String> findAllPushTokensByUserGuidNotIn(@Param(USER_GUIDS) Set<String> userGuids);

    /**
     * Find all push tokens.
     */
    @Query(value = "select ud.push_token from user_details ud where ud.push_token is not null " +
            GRANTED_CHECK, nativeQuery = true)
    Set<String> findAllPushTokens();
}
