package io.virusafe.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.dto.AccessTokenDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.NoSuchUserException;
import io.virusafe.exception.UnverifiableRefreshTokenException;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of token operations
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final JwtConfiguration jwtConfiguration;
    private final UserDetailsService userDetailsService;
    private final Clock systemClock;
    private final JwtParser jwtParser;

    /**
     * Construct a new TokenService, using the autowired beans.
     *
     * @param jwtConfiguration
     * @param userDetailsService
     * @param systemClock
     */
    @Autowired
    public TokenServiceImpl(final JwtConfiguration jwtConfiguration,
                            final UserDetailsService userDetailsService,
                            final Clock systemClock) {
        this.jwtConfiguration = jwtConfiguration;
        this.userDetailsService = userDetailsService;
        this.systemClock = systemClock;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(jwtConfiguration.getSecretKey()).build();
    }

    @Override
    public AccessTokenDTO generateToken(final String phoneNumber) {
        UserDetails userDetails = userDetailsService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NoSuchUserException(phoneNumber));

        String tokenSecret = UUID.randomUUID().toString();
        String accessToken = buildAccessToken(userDetails, tokenSecret);

        String refreshToken = buildRefreshToken(userDetails);

        String refreshTokenHash = null;
        try {
            MessageDigest encodingMessageDigest = MessageDigest.getInstance(jwtConfiguration.getHashAlgorithm());
            refreshTokenHash = Base64.getEncoder()
                    .encodeToString(encodingMessageDigest.digest(
                            refreshToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not create encoder so won't save refresh token hash to the DB: {}", e.getMessage());
        }

        userDetailsService.updateTokenDetails(userDetails.getUserGuid(), tokenSecret, refreshTokenHash);

        return AccessTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AccessTokenDTO refreshToken(final String refreshToken) {
        try {
            MessageDigest encodingMessageDigest = MessageDigest.getInstance(jwtConfiguration.getHashAlgorithm());
            String refreshTokenHash = Base64.getEncoder()
                    .encodeToString(encodingMessageDigest.digest(
                            refreshToken.getBytes(StandardCharsets.UTF_8)));

            UserDetails userDetails = userDetailsService.findByRefreshToken(refreshTokenHash)
                    .orElseThrow(UnverifiableRefreshTokenException::new);

            // Return a new token only if the current one is valid.
            if (isValidRefreshToken(refreshToken, userDetails)) {
                String tokenSecret = UUID.randomUUID().toString();
                String accessToken = buildAccessToken(userDetails, tokenSecret);

                userDetailsService.updateTokenDetails(userDetails.getUserGuid(), tokenSecret, refreshTokenHash);

                return AccessTokenDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                throw new UnverifiableRefreshTokenException();
            }
        } catch (NoSuchAlgorithmException | JwtException e) {
            // Reroute all expected exceptions to unverifiable refresh token exceptions so they can be mapped to response 438.
            log.error("Exception occurred while verifying refresh token: {}", e.getMessage());
            throw new UnverifiableRefreshTokenException(e);
        }
    }

    private boolean isValidRefreshToken(final String refreshToken, final UserDetails userDetails) {
        final Jws<Claims> refreshJws = jwtParser.parseClaimsJws(refreshToken);
        if (!jwtConfiguration.getAlgorithm().equals(refreshJws.getHeader().getAlgorithm())) {
            return false;
        }
        Claims claims = refreshJws.getBody();
        String refreshUserGuid = claims.get(jwtConfiguration.getUserGuidClaim(), String.class);
        return !Objects.isNull(refreshUserGuid) && refreshUserGuid.equals(userDetails.getUserGuid());
    }

    private String buildAccessToken(final UserDetails userDetails, final String tokenSecret) {
        LocalDateTime expirationDateTime = LocalDateTime.now(systemClock)
                .plusSeconds(jwtConfiguration.getTokenValidity());
        Date expiration = Date.from(expirationDateTime.atZone(systemClock.getZone()).toInstant());

        return Jwts.builder()
                .claim(jwtConfiguration.getCreatedClaim(), Long.toString(userDetails.getCreatedDate()))
                .claim(jwtConfiguration.getUserGuidClaim(), userDetails.getUserGuid())
                .claim(jwtConfiguration.getSecretClaim(), tokenSecret)
                .claim(jwtConfiguration.getNumberClaim(), userDetails.getPhoneNumber())
                .setExpiration(expiration)
                .signWith(jwtConfiguration.getSecretKey())
                .compact();
    }

    private String buildRefreshToken(final UserDetails userDetails) {
        LocalDateTime refreshTokenExpirationDateTime = LocalDateTime.now(systemClock)
                .plusSeconds(jwtConfiguration.getRefreshValidity());
        Date refreshTokenExpiration = Date.from(
                refreshTokenExpirationDateTime.atZone(systemClock.getZone()).toInstant());
        return Jwts.builder()
                .claim(jwtConfiguration.getUserGuidClaim(), userDetails.getUserGuid())
                .setExpiration(refreshTokenExpiration)
                .signWith(jwtConfiguration.getSecretKey())
                .compact();
    }
}
