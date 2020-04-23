package io.virusafe.security.filter;

import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.exception.UnregisteredClientIdException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Client ID verification filter
 */
public class ClientIdFilter extends OncePerRequestFilter {

    private final JwtConfiguration jwtConfiguration;

    /**
     * Construct client id verification filter
     *
     * @param jwtConfiguration
     */
    public ClientIdFilter(final JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String clientId = request.getHeader(jwtConfiguration.getClientIdHeaderName());

        if (Objects.isNull(clientId) || !jwtConfiguration.getClientIds().contains(clientId)) {
            throw new UnregisteredClientIdException();
        }

        filterChain.doFilter(request, response);
    }
}
