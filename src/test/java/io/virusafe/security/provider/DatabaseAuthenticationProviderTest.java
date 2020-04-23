package io.virusafe.security.provider;

import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.entity.AuthenticationUser;
import io.virusafe.service.authentication.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseAuthenticationProviderTest {

    private static final String HASH_ALGORITHM = "SHA-512";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String PASSWORD_HASH = "kRsKB6jKz+vF8fRVltZwFxNslQSZ+ltP9vr/oDHzzsfxl4U9FmBxLBVOH1nGD2guNOqbXL0tjVrbDINPlj8w3g==";
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String INVALID = "INVALID";

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private JwtConfiguration jwtConfiguration;

    private DatabaseAuthenticationProvider databaseAuthenticationProvider;

    @BeforeEach
    public void setUp() {
        databaseAuthenticationProvider = new DatabaseAuthenticationProvider(authenticationService, jwtConfiguration);
    }

    @Test
    public void testSupportsUsernamePasswordAuthentication() {
        assertTrue(databaseAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticateSuccessfully() {
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        AuthenticationUser authenticationUser = AuthenticationUser.builder()
                .username(USERNAME)
                .password(PASSWORD_HASH)
                .active(Boolean.TRUE)
                .build();
        when(authenticationService.findByUsernameAndPasswordAndActiveTrue(USERNAME, PASSWORD_HASH))
                .thenReturn(Optional.of(authenticationUser));
        final Authentication authentication =
                databaseAuthenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, null));

        List<GrantedAuthority> expectedAuthorities = AuthorityUtils.createAuthorityList(ADMIN_ROLE, USER_ROLE);
        assertAll(
                () -> assertNotNull(authentication),
                () -> assertTrue(authentication instanceof UsernamePasswordAuthenticationToken),
                () -> assertTrue(authentication.isAuthenticated()),
                () -> assertEquals(USERNAME, authentication.getName()),
                () -> assertEquals(PASSWORD_HASH, authentication.getCredentials().toString()),
                () -> assertTrue(authentication.getAuthorities().containsAll(expectedAuthorities))
        );
    }

    @Test
    public void testAuthenticateFailsWhenNoUserFound() {
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(HASH_ALGORITHM);
        when(authenticationService.findByUsernameAndPasswordAndActiveTrue(USERNAME, PASSWORD_HASH))
                .thenReturn(Optional.empty());
        final Authentication authentication =
                databaseAuthenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, null));
        assertNull(authentication);
    }

    @Test
    public void testAuthenticateFailsWhenHashingFails() {
        when(jwtConfiguration.getHashAlgorithm()).thenReturn(INVALID);
        final Authentication authentication =
                databaseAuthenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, null));
        assertNull(authentication);
    }
}