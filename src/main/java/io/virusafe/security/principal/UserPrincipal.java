package io.virusafe.security.principal;

import lombok.Builder;
import lombok.Data;

/**
 * Spring security User Principal object
 */
@Data
public class UserPrincipal {

    private Long userId;

    private String phoneNumber;

    private String userGuid;

    private String identificationNumber;

    /**
     * Create user principal
     *
     * @param userId
     * @param phoneNumber
     * @param userGuid
     * @param identificationNumber
     */
    @Builder
    public UserPrincipal(final Long userId, final String phoneNumber, final String userGuid,
                         final String identificationNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.userGuid = userGuid;
        this.identificationNumber = identificationNumber;
    }
}
