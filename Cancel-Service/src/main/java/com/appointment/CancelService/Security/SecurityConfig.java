package com.appointment.CancelService.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    public static final String ADMIN = "client_admin";
    public static final String DOCTOR = "client_doctor";
    public static final String USER = "client_user";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(auth ->
                {
                    auth.requestMatchers(HttpMethod.POST, "/api/cancel/*").hasAnyRole(DOCTOR,USER);
                    auth.requestMatchers(HttpMethod.GET, "/api/cancel/getAllCancel",
                            "/getByTransactionId/*","/getById/*").hasRole(ADMIN);
                    auth.requestMatchers(HttpMethod.GET, "/actuator/*").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/cancel/getAllCancelOfPatient").hasRole(USER);
                    auth.requestMatchers(HttpMethod.DELETE, "/api/cancel/delete/*").hasAnyRole(ADMIN);
                    auth.requestMatchers(HttpMethod.PUT, "/api/cancel/update/*").hasAnyRole(ADMIN,USER);
                    auth.anyRequest().authenticated();
                });

        http.
                oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)
                ));

        http.
                sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
