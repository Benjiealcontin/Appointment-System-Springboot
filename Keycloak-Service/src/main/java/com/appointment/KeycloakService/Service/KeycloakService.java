package com.appointment.KeycloakService.Service;

import com.appointment.KeycloakService.Exception.KeycloakException;
import com.appointment.KeycloakService.Request.FormRequest;
import com.appointment.KeycloakService.Response.TokenResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class KeycloakService {

    private final WebClient.Builder webClientBuilder;

    public KeycloakService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String extractToken(String bearerToken) {
        // Assuming the Authorization header looks like "Bearer <token>"
        String[] tokenParts = bearerToken.split(" ");
        if (tokenParts.length == 2 && "Bearer".equals(tokenParts[0])) {
            return tokenParts[1];
        } else {
            throw new IllegalArgumentException("Invalid Bearer token");
        }
    }


    public void decodeAndPrintToken(String token) {
        if (token != null) {
            // Decode the JWT token
            DecodedJWT decodedJWT = JWT.decode(token);

            // Extract information from the token
            String subject = decodedJWT.getSubject();
            Date expiration = decodedJWT.getExpiresAt();
            // You can extract other claims or information from the token as needed.

            System.out.println("Subject: " + subject);
            System.out.println("Expiration: " + expiration);
        }
    }

    public Mono<String> getToken(FormRequest formRequest) {
        // Replace "yourClientId" and "yourClientSecret" with the actual values
        String clientId = "Appointment-System-Client";
        String clientSecret = "4wcMr3Tcfzion3DpINSccGwvs4Hd3ioM";
        String grantType = "password";

        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/realms/Appointment/protocol/openid-connect/token")
                .body(BodyInserters
                        .fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", grantType)
                        .with("username", formRequest.getUsername())
                        .with("password", formRequest.getPassword()))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            // Get the error message from the response body if available
                            return response.bodyToMono(String.class)
                                    .flatMap(errorMessage -> {
                                        String customErrorMessage = "Error occurred during token retrieval: " + errorMessage;
                                        return Mono.error(new KeycloakException(customErrorMessage));
                                    });
                        }
                )
                .bodyToMono(TokenResponse.class)
                .doOnNext(tokenResponse -> {
                    String accessToken = tokenResponse.getAccess_token();
                    System.out.println("Token: " + accessToken);
                })
                .map(TokenResponse::getAccess_token);

    }
}
