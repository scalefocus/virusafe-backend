package io.virusafe.security.mdc;

import io.virusafe.security.principal.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MdcFilterTest {

    private static final String USER_GUID = "USER_GUID";
    private static final String PRINCIPAL = "PRINCIPAL";
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    private final MdcFilter mdcFilter = new MdcFilter();

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userGuid(USER_GUID)
                .build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mdcFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        // Verify MDC is cleared
        assertAll(
                () -> assertNull(MDC.get("CorrelationID")),
                () -> assertNull(MDC.get("UserID"))
        );
    }


    @Test
    public void testDoFilterInternalDoesntBreakChainWithMissingId() throws ServletException, IOException {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mdcFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        // Verify MDC is cleared
        assertAll(
                () -> assertNull(MDC.get("CorrelationID")),
                () -> assertNull(MDC.get("UserID"))
        );
    }

    @Test
    public void testDoFilterInternalDoesntBreakChainWithDifferentPrincipal() throws ServletException, IOException {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                PRINCIPAL, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mdcFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        // Verify MDC is cleared
        assertAll(
                () -> assertNull(MDC.get("CorrelationID")),
                () -> assertNull(MDC.get("UserID"))
        );
    }

    @Test
    public void testDoFilterInternalDoesntBreakChainForUnauthenticated() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(null);

        mdcFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        // Verify MDC is cleared
        assertAll(
                () -> assertNull(MDC.get("CorrelationID")),
                () -> assertNull(MDC.get("UserID"))
        );
    }
}