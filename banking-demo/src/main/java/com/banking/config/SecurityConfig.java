package com.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration.
 *
 * CONCEPTS:
 * - SecurityFilterChain (Spring Security 6+): replaces WebSecurityConfigurerAdapter.
 * - STATELESS session: no HTTP sessions — each request must be authenticated.
 *   Required for REST APIs (with JWT/OAuth2 in production).
 * - CSRF disabled for stateless APIs (no session cookie = no CSRF risk).
 *
 * In a production banking app:
 * - Add JWT filter: .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
 * - Add OAuth2 resource server: .oauth2ResourceServer(oauth2 -> oauth2.jwt(...))
 * - Add method security: @EnableMethodSecurity → @PreAuthorize on service methods
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // Safe for stateless REST APIs
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public: H2 console for development
                .requestMatchers("/h2-console/**").permitAll()
                // Public: health checks
                .requestMatchers("/actuator/health").permitAll()
                // Public: all API (for demo — lock down in production)
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            // Allow H2 console iframe
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
