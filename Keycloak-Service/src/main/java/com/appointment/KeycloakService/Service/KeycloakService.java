package com.appointment.KeycloakService.Service;

import com.appointment.KeycloakService.Exception.CustomConflictException;
import com.appointment.KeycloakService.Exception.KeycloakException;
import com.appointment.KeycloakService.Request.*;
import com.appointment.KeycloakService.Response.TokenResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;


@Service
public class KeycloakService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
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

    //Get Access Token
    public Mono<String> getToken(FormRequest formRequest) {
        // Replace "yourClientId" and "yourClientSecret" with the actual values
        String clientId = "Admin-clients";
        String clientSecret = "RERxD0VY1VrzYNZCpTv5Vy8TouAm5qmr";
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

    //Create Doctor
    public void createDoctor(Doctor doctor, String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/admin/realms/Appointment/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(BodyInserters.fromValue(doctor))
                .retrieve()
                .bodyToMono(Void.class);
        try {
            response.block(); // This blocks until the request completes
        } catch (Exception ex) {
            throw new CustomConflictException("User exists with same username");
        }
    }

    //Create Patient
    public void createPatient(Patient patient, String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/admin/realms/Appointment/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(BodyInserters.fromValue(patient))
                .retrieve()
                .bodyToMono(Void.class);

            response.block(); // This blocks until the request completes
    }

    //Get User
    public Patient getUser(String sub, String bearerToken) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",sub)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(Patient.class).block();
    }

    //Get Doctor
    public Doctor getDoctor(String sub, String bearerToken) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",sub)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(Doctor.class).block();
    }

    //Get All Doctor
    public List<GetAllDoctor> getDoctorsInGroup(String bearerToken) {
        String groupId = "f9e44dd8-1f49-4e38-8474-25a3cbdf71e0";

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/admin/realms/Appointment/groups/"+ groupId +"/members")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(GetAllDoctor.class)
                .collectList()
                .block();
    }

    //Get All Patients
    public List<GetAllPatient> getPatientsInGroup(String bearerToken) {
        String groupId = "fdd17119-a1c5-4b21-a6b9-75fcfaf0ac5e";

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/admin/realms/Appointment/groups/"+ groupId +"/members")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(GetAllPatient.class)
                .collectList()
                .block();
    }



    //Delete User
    public void deleteUser(String sub, String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .delete()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",sub)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(Void.class);

            response.block();
    }

    //Delete Doctor
    public void deleteDoctor(String sub, String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .delete()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",sub)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToMono(Void.class);
        response.block();
    }

    //Update Doctor
    public void updateDoctor(String userId, Doctor updatedDoctor,String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .put()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(BodyInserters.fromValue(updatedDoctor))
                .retrieve()
                .bodyToMono(Void.class);

        response.block();
    }

    //Update Patient
    public void updatePatient(String userId, Patient updatedPatient,String bearerToken) {
        Mono<Void> response = webClientBuilder.build()
                .put()
                .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(BodyInserters.fromValue(updatedPatient))
                .retrieve()
                .bodyToMono(Void.class);

        response.block();
    }
}
