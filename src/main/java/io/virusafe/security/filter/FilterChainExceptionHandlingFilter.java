package io.virusafe.security.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Exception handling filter that use global exception filter
 */
public class FilterChainExceptionHandlingFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;

    /**
     * Construct exception handling filter
     *
     * @param exceptionResolver
     */
    public FilterChainExceptionHandlingFilter(final HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        // Propagate any filter chain errors to the global controller advice exception handler
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
