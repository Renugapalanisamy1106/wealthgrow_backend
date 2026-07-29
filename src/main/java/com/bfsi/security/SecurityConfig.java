package com.bfsi.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Central Spring Security configuration.
 *
 * - Stateless (JWT) — no server-side sessions.
 * - Public: /auth/** (login, register), Swagger docs.
 * - Everything else requires a valid token, with role-based rules per area.
 *
 * Role IDs used in the system:
 *   ADMIN, PORTFOLIO_MANAGER, BUSINESS_ANALYST, OPERATIONS,
 *   COMPLAINTS_MANAGER, INVESTOR
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ---- Public endpoints ----
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                // ---- Role-based areas ----
                // Admin owns /admin/** and the admin side of fund-risk
                .requestMatchers("/admin/fund-risk/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Business Analyst owns /ba/** and the BA side of fund-risk
                .requestMatchers("/ba/fund-risk/**").hasRole("BUSINESS_ANALYST")
                // Read-only scenario list is also used by the investor scenario-analysis page
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/ba/scenarios")
                        .hasAnyRole("BUSINESS_ANALYST", "INVESTOR")
                .requestMatchers("/ba/**").hasRole("BUSINESS_ANALYST")

                // Portfolio Manager
                .requestMatchers("/portfolio-manager/**").hasRole("PORTFOLIO_MANAGER")

                // Operations
                .requestMatchers("/operations/**").hasRole("OPERATIONS")

                // Shared read: the funds list is used by the Operations NAV page too,
                // so allow both Investor and Operations to read it.
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/investor/funds")
                        .hasAnyRole("INVESTOR", "OPERATIONS", "BUSINESS_ANALYST")

                // Complaints Manager owns the complaints workspace.
                // Investors create complaints through /investor/complaint (below).
                .requestMatchers("/complaints/**").hasRole("COMPLAINTS_MANAGER")

                // Investor
                .requestMatchers("/investor/**").hasRole("INVESTOR")

                // Notifications are shared — any authenticated user can read/ack their own
                .requestMatchers("/notifications/**").authenticated()

                // Anything else must at least be authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS for the Angular dev server. Mirrors the old WebConfig but is now
     * driven through Spring Security's filter chain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
