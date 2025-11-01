package dev.dev_store_api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final Set<String> METHODS_TO_LOG = Set.of("POST", "PUT", "DELETE");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();

        if (METHODS_TO_LOG.contains(method)) {
            Principal principal = request.getUserPrincipal();
            String user;
            if (principal != null) {
                user = principal.getName();
            } else {
                user = "IP:" + request.getRemoteAddr();
            }
            log.info("Request Triggered: User=[{}], Method=[{}], URI=[{}]",
                    user,
                    method,
                    request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
