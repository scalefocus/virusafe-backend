package io.virusafe.service.authentication;

import io.virusafe.domain.entity.AuthenticationUser;
import io.virusafe.repository.AuthenticationUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    @Mock
    private AuthenticationUserRepository authenticationUserRepository;

    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void setUp() {
        this.authenticationService = new AuthenticationServiceImpl(authenticationUserRepository);
    }

    @Test
    public void testFindByUsernameAndPassword() {
        AuthenticationUser authenticationUser = AuthenticationUser.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .active(Boolean.TRUE)
                .build();
        when(authenticationUserRepository.findByUsernameAndPasswordAndActiveTrue(USERNAME, PASSWORD))
                .thenReturn(Optional.of(authenticationUser));

        final Optional<AuthenticationUser> foundAuthenticationUser =
                authenticationService.findByUsernameAndPasswordAndActiveTrue(USERNAME, PASSWORD);

        assertAll(
                () -> assertTrue(foundAuthenticationUser.isPresent()),
                () -> assertEquals(USERNAME, foundAuthenticationUser.get().getUsername()),
                () -> assertEquals(PASSWORD, foundAuthenticationUser.get().getPassword()),
                () -> assertTrue(foundAuthenticationUser.get().getActive())
        );
    }
}