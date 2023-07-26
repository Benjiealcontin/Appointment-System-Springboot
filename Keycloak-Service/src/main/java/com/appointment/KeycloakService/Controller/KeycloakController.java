package com.appointment.KeycloakService.Controller;

import com.appointment.KeycloakService.Service.KeycloakService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.awt.image.DataBuffer;
import java.util.Date;

@RestController
@RequestMapping("/api/keycloak")
public class KeycloakController {

    @Autowired
    private KeycloakService keycloakService;

    @GetMapping("/home")
    public String home(@RequestHeader("Authorization") String bearerToken) {
        String token = keycloakService.extractToken(bearerToken);
         keycloakService.decodeAndPrintToken(token);
        return "home";
    }

}
