package com.appointment.Approve_Service.Security;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"api/approve/*","api/approve/disapprove/*").hasRole(DOCTOR)
                        .requestMatchers(HttpMethod.GET,"/api/approve/getAllApprove","/api/approve/findById/*").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"api/approve/delete/*").hasAnyRole(ADMIN,DOCTOR)
                        .requestMatchers(HttpMethod.PUT,"api/approve/update/*").hasAnyRole(ADMIN,DOCTOR)
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
