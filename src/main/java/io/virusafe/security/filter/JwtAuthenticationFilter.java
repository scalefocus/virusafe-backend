package io.virusafe.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.virusafe.configuration.JwtConfiguration;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.exception.InvalidClaimJwtException;
import io.virusafe.exception.InvalidSignatureJwtException;
import io.virusafe.security.principal.UserPrincipal;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * JWT validation filter
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String CREATED_DATE_CLAIM_READABLE = "created date";
    public static final String GUID_CLAIM_READABLE = "user GUID";
    public static final String SECRET_CLAIM_READABLE = "token secret";

    private final JwtConfiguration jwtConfiguration;

    private final JwtParser jwtParser;

    private final UserDetailsService userDetailsService;

    /**
     * Constructor JWT validation filter using beans.
     *
     * @param jwtConfiguration
     * @param userDetailsService
     */
    public JwtAuthenticationFilter(final JwtConfiguration jwtConfiguration,
                                   final UserDetailsService userDetailsService) {
        this.jwtConfiguration = jwtConfiguration;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(jwtConfiguration.getSecretKey()).build();
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(jwtConfiguration.getAuthorizationHeaderName());

        // If authorization header is missing or not a bearer, move to next filter.
        if (Objects.isNull(authorizationHeader) ||
                !authorizationHeader.startsWith(jwtConfiguration.getBearerPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace(jwtConfiguration.getBearerPrefix(), "");

        // Parse token, throwing exception if it can't be parsed, contains invalid claims,
        // or is signed with an invalid algorithm or key.
        final Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        if (!jwtConfiguration.getAlgorithm().equals(jws.getHeader().getAlgorithm())) {
            throw new InvalidSignatureJwtException();
        }
        Claims claims = jws.getBody();

        String userGuid = claims.get(jwtConfiguration.getUserGuidClaim(), String.class);
        if (Objects.isNull(userGuid)) {
            throw new InvalidClaimJwtException(GUID_CLAIM_READABLE);
        }
        Long createdOn = parseCreatedOn(claims);

        UserDetails dbUser = getDbUserByGuid(userGuid);

        validateCreatedOn(createdOn, dbUser);
        validateTokenSecret(claims, dbUser);

        setPrincipalToSpringSecurityContext(dbUser);

        filterChain.doFilter(request, response);
    }

    private UserDetails getDbUserByGuid(final String userGuid) {
        return userDetailsService.findByUserGuid(userGuid).orElseThrow(
                () -> new InvalidClaimJwtException(GUID_CLAIM_READABLE)
        );
    }

    private void validateTokenSecret(final Claims claims, final UserDetails dbUser) {
        String tokenSecret = claims.get(jwtConfiguration.getSecretClaim(), String.class);
        if (Objects.isNull(tokenSecret) || !tokenSecret.equals(dbUser.getTokenSecret())) {
            throw new InvalidClaimJwtException(SECRET_CLAIM_READABLE);
        }
    }

    private void validateCreatedOn(final Long createdOn, final UserDetails dbUser) {
        if (!createdOn.equals(dbUser.getCreatedDate())) {
            log.error(
                    "Creation date [{}] different than expected [{}]", createdOn, dbUser.getCreatedDate());

            throw new InvalidClaimJwtException(CREATED_DATE_CLAIM_READABLE);
        }
    }

    private Long parseCreatedOn(final Claims claims) {
        try {
            return Long.valueOf(claims.get(jwtConfiguration.getCreatedClaim(), String.class));
        } catch (NumberFormatException nfe) {
            throw new InvalidClaimJwtException(CREATED_DATE_CLAIM_READABLE, nfe);
        }
    }

    private void setPrincipalToSpringSecurityContext(final UserDetails dbUser) {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(dbUser.getId())
                .phoneNumber(dbUser.getPhoneNumber())
                .userGuid(dbUser.getUserGuid())
                .identificationNumber(dbUser.getIdentificationNumberPlain())
                .build();

        // Build UsernamePasswordAuthenticationToken, setting the fetched DB user as principal.
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, Collections.emptyList());

        // Set authentication in the context.
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
