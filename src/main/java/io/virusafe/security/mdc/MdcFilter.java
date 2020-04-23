package io.virusafe.security.mdc;

import io.virusafe.security.principal.UserPrincipal;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * MDC parameter to the log to may connect log lines
 */
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put("CorrelationId", getCorrelationId());
            // Set the UserPhoneNumber field if we've set a current user principal.
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.nonNull(authentication)) {
                final Object principal = authentication.getPrincipal();
                if (principal instanceof UserPrincipal) {
                    String userId = ((UserPrincipal) principal).getUserGuid();
                    MDC.put("UserId", userId);
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.remove("CorrelationId");
            MDC.remove("UserId");

        }
    }

    private String getCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
