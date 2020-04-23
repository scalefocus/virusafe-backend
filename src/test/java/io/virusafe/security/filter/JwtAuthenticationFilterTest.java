package io.virusafe.security.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.InvalidClaimJwtException;
import io.virusafe.exception.InvalidSignatureJwtException;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String INVALID_GUID = "INVALID_GUID";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String CREATED_CLAIM = "CREATED_CLAIM";
    private static final String SECRET_CLAIM = "SECRET_CLAIM";
    private static final String USER_GUID_CLAIM = "USER_GUID_CLAIM";
    private static final String SECRET_KEY = "M3VVd1RXalhZN3I0SldjaWlnMXUyclRIcmVEdFc2TnY4N3o2MzMwcDl2ZzlWYlhrbmFDRnBFWllER3UzUXYwbmx3ZF8tSlF3S3JiRU5hQ09zVGxqcHJTUXl1R0FvQVQ2NXNWUlhJUkpHMFpGYUdVOWk1ejQ5TUFnQjgzdUIzSnBDc1VTVGhqOHdvRU1MeFI2dUI5YThPMjZuVGN5blFmY3FkOWdYRkV6elJiSVNhaF9PWEdlOFJLQ1AyaG1BenBhSHNjMFdmdXlMdmxuMG5mZUczNXhfRktyOWdVMWVjOTZwVjNBX3lXaFNHdHlRRWkwS2pvRXRIMW5WYkxmelR2SmdJX1VYeVBrMzNNVE9rMm1ETGl4SnVPbWRSNUc3Nnk1WVlTSWtJQm9GbjN0dkFNYV93UVRkZnV0NS0zSnpyVGVURkxCSVBNOWhTVURnTHp2U1NyV1Nn";
    private static final String ALGORITHM = "HS512";
    private static final String USER_GUID = "USER_GUID";
    private static final String OTHER_ALGORITHM = "HS256";
    private static final String AUTHORIZATION_HEADER = "AUTHORIZATION_HEADER";
    private static final String BEARER_PREFIX = "BEARER_PREFIX";
    private static final Long CREATED_VALUE = 1L;
    private static final String TOKEN_SECRET_KEY = "TOKEN_SECRET_KEY";
    private static final Long USER_ID = 1L;
    private static final String IDENTIFICATION_NUMBER = "IDENTIFICATION_NUMBER";
    private static final Long INVALID_CREATED_VALUE = 2L;
    private static final String INVALID_SECRET_KEY = "INVALID_SECRET_KEY";

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private JwtConfiguration jwtConfiguration;
    @Mock
    private UserDetailsService userDetailsService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtParser jwtParser;

    @BeforeEach
    public void setUp() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        when(jwtConfiguration.getSecretKey()).thenReturn(secretKeySpec);
        when(jwtConfiguration.getAuthorizationHeaderName()).thenReturn(AUTHORIZATION_HEADER);
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtConfiguration.getSecretKey()).build();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtConfiguration, userDetailsService);
    }

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        UserDetails dbUser = UserDetails.builder()
                .id(USER_ID)
                .userGuid(USER_GUID)
                .tokenSecret(TOKEN_SECRET_KEY)
                .phoneNumber(PHONE_NUMBER)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .build();
        dbUser.setCreatedDate(CREATED_VALUE);

        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(dbUser));

        jwtAuthenticationFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);

        // Check UserPrincipal is set correctly.
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertAll(
                () -> assertEquals(USER_ID, principal.getUserId()),
                () -> assertEquals(USER_GUID, principal.getUserGuid()),
                () -> assertEquals(PHONE_NUMBER, principal.getPhoneNumber()),
                () -> assertEquals(IDENTIFICATION_NUMBER, principal.getIdentificationNumber())
        );
    }

    @Test
    public void testDoFilterInternalDoesntBreakChainForMissingAuthorizationHeader() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalDoesntBreakChainForNonBearerAuthorizationHeader() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionForInvalidAlgorithm() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(OTHER_ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec, SignatureAlgorithm.forName(OTHER_ALGORITHM))
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        assertThrows(InvalidSignatureJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionForMissingGuidClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionForMismatchingGuidClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, INVALID_GUID)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        when(userDetailsService.findByUserGuid(INVALID_GUID)).thenReturn(Optional.empty());

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }


    @Test
    public void testDoFilterInternalThrowsExceptionForMissingCreatedClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionForMismatchingCreatedClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(CREATED_CLAIM, INVALID_CREATED_VALUE.toString())
                .claim(SECRET_CLAIM, TOKEN_SECRET_KEY)
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        UserDetails dbUser = UserDetails.builder()
                .id(USER_ID)
                .userGuid(USER_GUID)
                .tokenSecret(TOKEN_SECRET_KEY)
                .phoneNumber(PHONE_NUMBER)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .build();
        dbUser.setCreatedDate(CREATED_VALUE);

        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(dbUser));

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionForMissingSecretClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        UserDetails dbUser = UserDetails.builder()
                .id(USER_ID)
                .userGuid(USER_GUID)
                .tokenSecret(TOKEN_SECRET_KEY)
                .phoneNumber(PHONE_NUMBER)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .build();
        dbUser.setCreatedDate(CREATED_VALUE);

        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(dbUser));

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }



    @Test
    public void testDoFilterInternalThrowsExceptionForMismatchingSecretClaim() throws ServletException, IOException {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(ALGORITHM).getJcaName());
        String token = Jwts.builder()
                .claim(USER_GUID_CLAIM, USER_GUID)
                .claim(SECRET_CLAIM, INVALID_SECRET_KEY)
                .claim(CREATED_CLAIM, CREATED_VALUE.toString())
                .signWith(secretKeySpec)
                .compact();
        when(jwtConfiguration.getBearerPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtConfiguration.getAlgorithm()).thenReturn(ALGORITHM);
        when(jwtConfiguration.getUserGuidClaim()).thenReturn(USER_GUID_CLAIM);
        when(jwtConfiguration.getSecretClaim()).thenReturn(SECRET_CLAIM);
        when(jwtConfiguration.getCreatedClaim()).thenReturn(CREATED_CLAIM);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);

        UserDetails dbUser = UserDetails.builder()
                .id(USER_ID)
                .userGuid(USER_GUID)
                .tokenSecret(TOKEN_SECRET_KEY)
                .phoneNumber(PHONE_NUMBER)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .build();
        dbUser.setCreatedDate(CREATED_VALUE);

        when(userDetailsService.findByUserGuid(USER_GUID)).thenReturn(Optional.of(dbUser));

        assertThrows(InvalidClaimJwtException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }
}