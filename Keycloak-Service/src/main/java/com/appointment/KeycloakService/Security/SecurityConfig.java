package com.appointment.KeycloakService.Security;

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
    public static final String USER = "client_user";

    public static final String DOCTOR = "client_doctor";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"api/keycloak/get-token","api/keycloak/confirmation").permitAll()
                        .requestMatchers(HttpMethod.POST,"api/keycloak/AddDoctor","api/keycloak/AddPatient").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET,"api/keycloak/getPatient/*","api/keycloak/getDoctor/*").hasAnyRole(ADMIN,DOCTOR,USER)
                        .requestMatchers(HttpMethod.GET,"api/keycloak/getAllPatients","api/keycloak/getAllDoctor").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET,"api/keycloak/getInfo").hasRole(USER)
                        .requestMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"api/keycloak/deletePatient","api/keycloak/deleteDoctor").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT,"api/keycloak/update-doctor/*","api/keycloak/update-patient/*").hasRole(ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
