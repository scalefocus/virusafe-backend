package io.virusafe.service.userdetails;

import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.entity.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * User details service
 */
public interface UserDetailsService {
    /**
     * Find UserDetails by GUID
     *
     * @param userGuid
     * @return
     */
    Optional<UserDetails> findByUserGuid(String userGuid);

    /**
     * Find UserDetails by PhoneNumber
     *
     * @param phoneNumber
     * @return
     */
    Optional<UserDetails> findByPhoneNumber(String phoneNumber);

    /**
     * Find UserDetails by Phone number and valid PIN
     *
     * @param phoneNumber
     * @param pin
     * @param time
     * @return
     */
    Optional<UserDetails> findByPhoneNumberAndValidPin(String phoneNumber, String pin, LocalDateTime time);

    /**
     * Save UserDetails
     *
     * @param userDetails
     */
    void save(UserDetails userDetails);

    /**
     * Update personal information
     *
     * @param userGuid
     * @param personalInformationUpdateCommand
     */
    void updatePersonalInformation(String userGuid,
                                   PersonalInformationUpdateCommand personalInformationUpdateCommand);


    /**
     * Update push token
     *
     * @param userGuid
     * @param pushToken
     */
    void updatePushToken(String userGuid, String pushToken);

    /**
     * Update token details
     *
     * @param userGuid
     * @param tokenSecret
     * @param refreshTokenHash
     */
    void updateTokenDetails(String userGuid, String tokenSecret, String refreshTokenHash);

    /**
     * Find user details by refresh token
     *
     * @param refreshTokenHash
     * @return
     */
    Optional<UserDetails> findByRefreshToken(String refreshTokenHash);

    /**
     * Delete user details by ID
     *
     * @param userGuid
     */
    void deleteByUserGuid(String userGuid);

    /**
     * search pushTokens
     *
     * @return
     */
    Set<String> findPushTokensForUserGuids(final Set<String> userGuids, final boolean reverseQueryResults);
}
