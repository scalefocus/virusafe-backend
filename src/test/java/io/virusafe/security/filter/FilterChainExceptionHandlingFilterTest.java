package io.virusafe.security.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FilterChainExceptionHandlingFilterTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HandlerExceptionResolver exceptionResolver;

    private FilterChainExceptionHandlingFilter filterChainExceptionHandlingFilter;

    @BeforeEach
    public void setUp() {
        filterChainExceptionHandlingFilter = new FilterChainExceptionHandlingFilter(exceptionResolver);
    }

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {

        filterChainExceptionHandlingFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );

        // Verify chain isn't broken when no exception is thrown.
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        // Verify resolver isn't called when no exception is thrown.
        verify(exceptionResolver, times(0)).resolveException(
                eq(httpServletRequest), eq(httpServletResponse), any(), any(Exception.class));
    }

    @Test
    public void testDoFilterInternalInterceptsExceptions() throws ServletException, IOException {

        RuntimeException exception = new RuntimeException();
        doThrow(exception).when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        filterChainExceptionHandlingFilter.doFilterInternal(
                httpServletRequest,
                httpServletResponse,
                filterChain
        );
        // Verify resolver is called for the same exception.
        verify(exceptionResolver, times(1)).resolveException(
                httpServletRequest, httpServletResponse, null, exception);
    }
}