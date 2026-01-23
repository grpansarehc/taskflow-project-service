package com.taskflow.project_service.config;

import com.taskflow.project_service.security.GatewayTrustFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration for Project Service
 * Configures OAuth2 Resource Server for Keycloak JWT validation
 * and GatewayTrustFilter for extracting user context from headers
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private GatewayTrustFilter gatewayTrustFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                // Configure OAuth2 Resource Server for JWT validation
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // JWT decoder is auto-configured from application.properties
                        })
                );

        // Add GatewayTrustFilter to extract user info from headers
        http.addFilterBefore(gatewayTrustFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
