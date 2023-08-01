package com.appointment.KeycloakService.Controller;

import com.appointment.KeycloakService.Exception.KeycloakException;
import com.appointment.KeycloakService.Request.FormRequest;
import com.appointment.KeycloakService.Service.KeycloakService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.ModelAndView;
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

    @PostMapping("/get-token")
    public Mono<String> getToken(@RequestBody FormRequest formRequest) {
        return keycloakService.getToken(formRequest)
                .onErrorResume(KeycloakException.class, ex -> Mono.just(ex.getMessage()));
    }
}
