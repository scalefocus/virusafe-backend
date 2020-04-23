package io.virusafe.security.filter;

import io.virusafe.configuration.JwtConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientIdFilterTest {

    private static final String CLIENT_ID_HEADER = "CLIENT_ID_HEADER";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String INVALID_CLIENT_ID = "INVALID_CLIENT_ID";
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private JwtConfiguration jwtConfiguration;

    private ClientIdFilter clientIdFilter;

    @BeforeEach
    public void setUp() {
        when(jwtConfiguration.getClientIdHeaderName()).thenReturn(CLIENT_ID_HEADER);
        clientIdFilter = new ClientIdFilter(jwtConfiguration);
    }

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {

        when(jwtConfiguration.getClientIds()).thenReturn(Collections.singletonList(CLIENT_ID));
        when(httpServletRequest.getHeader(CLIENT_ID_HEADER)).thenReturn(CLIENT_ID);

        clientIdFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionOnMissingClientId() throws ServletException, IOException {

        when(httpServletRequest.getHeader(CLIENT_ID_HEADER)).thenReturn(null);

        assertThrows(SecurityException.class, () ->
                clientIdFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testDoFilterInternalThrowsExceptionOnInvalidClientId() throws ServletException, IOException {

        when(jwtConfiguration.getClientIds()).thenReturn(Collections.singletonList(CLIENT_ID));
        when(httpServletRequest.getHeader(CLIENT_ID_HEADER)).thenReturn(INVALID_CLIENT_ID);

        assertThrows(SecurityException.class, () ->
                clientIdFilter.doFilterInternal(
                        httpServletRequest,
                        httpServletResponse,
                        filterChain
                ));

        // Verify chain is broken.
        verify(filterChain, times(0)).doFilter(httpServletRequest, httpServletResponse);
    }
}