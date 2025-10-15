package com.example.authsystem.security.filter;


import com.example.authsystem.util.CacheUtilWithRedisson;
import com.example.authsystem.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.authsystem.constant.AppConstants.ACCESS_BLACKLIST_PREFIX;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    JWTUtil jwtUtil;
    UserDetailsService userDetailsService;
    CacheUtilWithRedisson cache;

    private boolean isSwaggerOrPublic(String uri) {
        return uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-ui.html")
                || uri.startsWith("/actuator")
                || uri.startsWith("/api/v1/auth/register")
                || uri.startsWith("/api/v1/auth/verify")
                || uri.startsWith("/api/v1/auth/login")
                || uri.startsWith("/api/v1/auth/forgot-password");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isSwaggerOrPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            if (cache.exists(ACCESS_BLACKLIST_PREFIX + token)) {
                response.sendError(SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }

            if (!jwtUtil.validate(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                filterChain.doFilter(request, response);
                return;
            }

            List<String> roles = jwtUtil.extractRoles(token);
            if (!roles.isEmpty()) {
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserDetails userDetails = User.withUsername(username)
                        .password("")
                        .authorities(authorities)
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("Failed to authenticate request with JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}