package io.virusafe.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.dto.AccessTokenDTO;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.NoSuchUserException;
import io.virusafe.exception.UnverifiableRefreshTokenException;
import io.virusafe.service.token.TokenServiceImpl;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String CREATED_CLAIM = "CREATED_CLAIM";
    private static final String NUMBER_CLAIM = "NUMBER_CLAIM";
    private static final String SECRET_CLAIM = "SECRET_CLAIM";
    private static final String USER_GUID_CLAIM = "USER_GUID_CLAIM";
    private static final String HASH_ALGORITHM = "SHA-512";
    private static final String SECRET_KEY = "M3VVd1RXalhZN3I0SldjaWlnMXUyclRIcmVEdFc2TnY4N3o2MzMwcDl2ZzlWYlhrbmFDRnBFWllER3UzUXYwbmx3ZF8tSlF3S3JiRU5hQ09zVGxqcHJTUXl1R0FvQVQ2NXNWUlhJUkpHMFpGYUdVOWk1ejQ5TUFnQjgzdUIzSnBDc1VTVGhqOHdvRU1MeFI2dUI5YThPMjZuVGN5blFmY3FkOWdYRkV6elJiSVNhaF9PWEdlOFJLQ1AyaG1BenBhSHNjMFdmdXlMdmxuMG5mZUczNXhfRktyOWdVMWVjOTZwVjNBX3lXaFNHdHlRRWkwS2pvRXRIMW5WYkxmelR2SmdJX1VYeVBrMzNNVE9rMm1ETGl4SnVPbWRSNUc3Nnk1WVlTSWtJQm9GbjN0dkFNYV93UVRkZnV0NS0zSnpyVGVURkxCSVBNOWhTVURnTHp2U1NyV1Nn";
    private static final String ALGORITHM = "HS512";
    private static final String USER_GUID = "USER_GUID";
    private static final String INVALID = "INVALID";
    private static final String OTHER_ALGORITHM = "HS256";

    private final Clock systemClock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );
    @Mock
    private JwtConfiguration jwtConfiguration;
    @Mock
    private UserDetailsService userDetailsService;
    private TokenServiceImpl tokenService;
    private JwtParser jwtParser;

    private final UserDetails userDetails = UserDetails.builder()
            .userGuid(USER_GUID)
            .phoneNumber(PHONE_NUMBER)
            .build();

    @BeforeEach
    public void setUp() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        when(jwtConfiguration.getSecretKey()).thenReturn(secretKeySpec);
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtConfiguration.getSecretKey()).build();
        tokenService = new TokenServiceImpl(jwtConfiguration, userDetailsService, systemClock);
    }

    @Test
    public void testGenerateToken() {
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(jwtConfiguration.getNumberClaim()).thenReturn(NUMBER_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(userDetails));

        AccessTokenDTO accessTokenDTO = tokenService.generateToken(PHONE_NUMBER);
        String accessToken = accessTokenDTO.getAccessToken();
        String refreshToken = accessTokenDTO.getRefreshToken();

        Jws<Claims> accessJws = jwtParser.parseClaimsJws(accessToken);
        Jws<Claims> refreshJws = jwtParser.parseClaimsJws(refreshToken);

        assertAll(
                () -> assertEquals(PHONE_NUMBER, accessJws.getBody().get(NUMBER_CLAIM)),
                () -> assertEquals(USER_GUID, accessJws.getBody().get(USER_GUID_CLAIM)),
                () -> assertEquals(ALGORITHM, accessJws.getHeader().getAlgorithm()),
                () -> assertEquals(USER_GUID, refreshJws.getBody().get(USER_GUID_CLAIM)),
                () -> assertEquals(ALGORITHM, refreshJws.getHeader().getAlgorithm())
        );
    }

    @Test
    public void testNoTokenGeneratedForMissingUser() {
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(NoSuchUserException.class, () -> tokenService.generateToken(PHONE_NUMBER));
    }

    @Test
    public void testTokenGeneratedForInvalidRefreshHashingAlgorithm() {
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(jwtConfiguration.getNumberClaim()).thenReturn(NUMBER_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(INVALID);
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(userDetails));

        AccessTokenDTO accessTokenDTO = tokenService.generateToken(PHONE_NUMBER);
        String accessToken = accessTokenDTO.getAccessToken();
        String refreshToken = accessTokenDTO.getRefreshToken();

        Jws<Claims> accessJws = jwtParser.parseClaimsJws(accessToken);
        Jws<Claims> refreshJws = jwtParser.parseClaimsJws(refreshToken);

        assertAll(
                () -> assertEquals(PHONE_NUMBER, accessJws.getBody().get(NUMBER_CLAIM)),
                () -> assertEquals(USER_GUID, accessJws.getBody().get(USER_GUID_CLAIM)),
                () -> assertEquals(ALGORITHM, accessJws.getHeader().getAlgorithm()),
                () -> assertEquals(USER_GUID, refreshJws.getBody().get(USER_GUID_CLAIM)),
                () -> assertEquals(ALGORITHM, refreshJws.getHeader().getAlgorithm())
        );
    }

    @Test
    public void testAccessTokenGeneratedFromRefreshToken() throws NoSuchAlgorithmException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String refreshToken = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .signWith(secretKeySpec)
                .compact();
        MessageDigest encodingMessageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String refreshTokenHash = Base64.getEncoder()
                .encodeToString(encodingMessageDigest.digest(
                        refreshToken.getBytes(StandardCharsets.UTF_8)));

        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(jwtConfiguration.getNumberClaim()).thenReturn(NUMBER_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(userDetailsService.findByRefreshToken(refreshTokenHash)).thenReturn(Optional.of(userDetails));

        AccessTokenDTO accessTokenDTO = tokenService.refreshToken(refreshToken);
        String accessToken = accessTokenDTO.getAccessToken();
        Jws<Claims> accessJws = jwtParser.parseClaimsJws(accessToken);

        assertAll(
                () -> assertEquals(PHONE_NUMBER, accessJws.getBody().get(NUMBER_CLAIM)),
                () -> assertEquals(USER_GUID, accessJws.getBody().get(USER_GUID_CLAIM)),
                () -> assertEquals(ALGORITHM, accessJws.getHeader().getAlgorithm()),
                () -> assertEquals(refreshToken, accessTokenDTO.getRefreshToken())
        );
    }

    @Test
    public void testNoAccessTokenGeneratedFromRefreshTokenWithInvalidHash() throws NoSuchAlgorithmException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String refreshToken = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .signWith(secretKeySpec)
                .compact();
        MessageDigest encodingMessageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String refreshTokenHash = Base64.getEncoder()
                .encodeToString(encodingMessageDigest.digest(
                        refreshToken.getBytes(StandardCharsets.UTF_8)));

        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(userDetailsService.findByRefreshToken(refreshTokenHash)).thenReturn(Optional.empty());

        assertThrows(UnverifiableRefreshTokenException.class, () -> tokenService.refreshToken(refreshToken));
    }

    @Test
    public void testUnverifiableTokenExceptionIsThrownForInvalidHashingAlgorithm() {
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(INVALID);
        assertThrows(UnverifiableRefreshTokenException.class, () -> tokenService.refreshToken(INVALID));
    }

    @Test
    public void testNoAccessTokenGeneratedFromRefreshTokenWithWrongAlgorithm() throws NoSuchAlgorithmException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(OTHER_ALGORITHM).getJcaName());
        String refreshToken = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .signWith(secretKeySpec, SignatureAlgorithm.forName(OTHER_ALGORITHM))
                .compact();
        MessageDigest encodingMessageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String refreshTokenHash = Base64.getEncoder()
                .encodeToString(encodingMessageDigest.digest(
                        refreshToken.getBytes(StandardCharsets.UTF_8)));

        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(userDetailsService.findByRefreshToken(refreshTokenHash)).thenReturn(Optional.of(userDetails));

        assertThrows(UnverifiableRefreshTokenException.class, () -> tokenService.refreshToken(refreshToken));
    }

    @Test
    public void testNoAccessTokenGeneratedFromRefreshTokenWithIncorrectGUID() throws NoSuchAlgorithmException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String refreshToken = Jwts.builder()
                .claim(USER_GUID_CLAIM, INVALID)
                .signWith(secretKeySpec)
                .compact();
        MessageDigest encodingMessageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String refreshTokenHash = Base64.getEncoder()
                .encodeToString(encodingMessageDigest.digest(
                        refreshToken.getBytes(StandardCharsets.UTF_8)));

        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(userDetailsService.findByRefreshToken(refreshTokenHash)).thenReturn(Optional.of(userDetails));

        assertThrows(UnverifiableRefreshTokenException.class, () -> tokenService.refreshToken(refreshToken));
    }

    @Test
    public void testNoAccessTokenGeneratedFromRefreshTokenWithNullGUID() throws NoSuchAlgorithmException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String refreshToken = Jwts.builder()
                .signWith(secretKeySpec)
                .claim(INVALID, INVALID)
                .compact();
        MessageDigest encodingMessageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        String refreshTokenHash = Base64.getEncoder()
                .encodeToString(encodingMessageDigest.digest(
                        refreshToken.getBytes(StandardCharsets.UTF_8)));

        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(userDetailsService.findByRefreshToken(refreshTokenHash)).thenReturn(Optional.of(userDetails));

        assertThrows(UnverifiableRefreshTokenException.class, () -> tokenService.refreshToken(refreshToken));
    }
}