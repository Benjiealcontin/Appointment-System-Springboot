package com.appoinment.DoctorService.Security;

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
        http.
                authorizeHttpRequests(auth ->
                {
                    auth.requestMatchers(HttpMethod.GET, "/actuator/*").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/doctor/findAll"
                    ,"/api/doctor/getDoctorById/*","/api/doctor/findByName/*","/api/doctor/findBySpecialization/*").hasAnyRole(ADMIN,USER,DOCTOR);
                    auth.requestMatchers(HttpMethod.POST, "/api/doctor/add").hasRole(ADMIN);
                    auth.requestMatchers(HttpMethod.DELETE, "/api/doctor/delete/*").hasRole(ADMIN);
                    auth.requestMatchers(HttpMethod.PUT, "/api/doctor/update/*").hasRole(ADMIN);
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
