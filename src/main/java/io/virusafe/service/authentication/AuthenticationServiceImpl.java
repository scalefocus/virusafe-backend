package io.virusafe.service.authentication;

import io.virusafe.domain.entity.AuthenticationUser;
import io.virusafe.repository.AuthenticationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationUserRepository authenticationUserRepository;

    /**
     * Construct a new AuthenticationServiceImpl, using the autowired AuthenticationUserRepository.
     *
     * @param authenticationUserRepository the AuthenticationUserRepository to use for communicating with the DB
     */
    @Autowired
    public AuthenticationServiceImpl(final AuthenticationUserRepository authenticationUserRepository) {
        this.authenticationUserRepository = authenticationUserRepository;
    }
    
    @Override
    public Optional<AuthenticationUser> findByUsernameAndPasswordAndActiveTrue(final String username,
                                                                               final String password) {
        return authenticationUserRepository.findByUsernameAndPasswordAndActiveTrue(username, password);
    }
}
