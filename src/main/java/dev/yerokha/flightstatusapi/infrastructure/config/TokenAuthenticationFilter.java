package dev.yerokha.flightstatusapi.infrastructure.config;

import dev.yerokha.flightstatusapi.infrastructure.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static dev.yerokha.flightstatusapi.infrastructure.util.EncryptionUtil.decrypt;
import static dev.yerokha.flightstatusapi.infrastructure.util.RedisUtil.getValue;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String accessToken = request.getHeader("Authorization");
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String username;

        try {
            username = tokenService.getUsernameFromTokenString(accessToken);
            String key = "access_token:" + username;
            String cachedToken = decrypt(getValue(key));
            String accessTokenValue = accessToken.substring(7);
            if (!accessTokenValue.equals(cachedToken)) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
