package io.virusafe.security.provider;

import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.entity.AuthenticationUser;
import io.virusafe.service.authentication.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DatabaseAuthenticationProvider implements AuthenticationProvider {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    private final AuthenticationService authenticationService;
    private final JwtConfiguration jwtConfiguration;

    /**
     * Construct a new DatabaseAuthenticationProvider, using the autowired AuthenticationService and
     * JwtConfiguration beans.
     *
     * @param authenticationService the AuthenticationService to use for validating user credentials
     * @param jwtConfiguration      the JwtConfiguration to use for hashing configuration
     */
    @Autowired
    public DatabaseAuthenticationProvider(final AuthenticationService authenticationService,
                                          final JwtConfiguration jwtConfiguration) {
        this.authenticationService = authenticationService;
        this.jwtConfiguration = jwtConfiguration;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        String password = authentication.getCredentials().toString();
        String passwordHash = null;
        try {
            MessageDigest encodingMessageDigest = MessageDigest.getInstance(jwtConfiguration.getHashAlgorithm());
            passwordHash = Base64.getEncoder()
                    .encodeToString(encodingMessageDigest.digest(
                            password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not encode password so authentication will fail {}", e.getMessage());
            return null;
        }
        String username = authentication.getName();
        Optional<AuthenticationUser> authenticationUser = authenticationService
                .findByUsernameAndPasswordAndActiveTrue(username, passwordHash);

        // Don't initialize authentication if user is not found.
        if (authenticationUser.isEmpty()) {
            return null;
        }
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(ADMIN_ROLE, USER_ROLE);
        return new UsernamePasswordAuthenticationToken(username, passwordHash, authorities);
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
