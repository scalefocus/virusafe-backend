package io.virusafe.service.token;

import io.virusafe.domain.dto.AccessTokenDTO;

/**
 * Support token operations
 */
public interface TokenService {
    /**
     * Generate token
     *
     * @param phoneNumber
     * @return
     */
    AccessTokenDTO generateToken(String phoneNumber);

    /**
     * Refresh token using client provided refresh token
     *
     * @param refreshToken
     * @return
     */
    AccessTokenDTO refreshToken(String refreshToken);
}
