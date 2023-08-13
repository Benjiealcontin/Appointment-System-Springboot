package com.appoinment.DoctorService.Service;



import com.appoinment.DoctorService.Exception.AddDoctorException;

import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Request.Doctor;


import com.appoinment.DoctorService.Request.GetAllDoctor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;


@Service
public class DoctorService {

    private final WebClient.Builder webClientBuilder;

    public DoctorService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    //Add doctor
    public void createDoctor(Doctor doctor, String bearerToken) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8081/admin/realms/Appointment/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .body(BodyInserters.fromValue(doctor))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); // This blocks until the request completes

        } catch (WebClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                if (jsonNode.isObject()) {
                    if (jsonNode.has("errorMessage")) {
                        String errorMessage = jsonNode.get("errorMessage").asText();
                        throw new AddDoctorException("Keycloak API Error: " + errorMessage);
                    }
                }

                throw new AddDoctorException("An error occurred: " + responseBody);
            } catch (IOException ex) {
                throw new AddDoctorException("An error occurred while parsing the response: " + responseBody);
            }
        } catch (Exception e) {
            throw new AddDoctorException("An unexpected error occurred: " + e.getMessage());
        }
    }

    //Get Doctor by I'd
    public Doctor getDoctor(String userId, String bearerToken) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/admin/realms/Appointment/users/{id}", userId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .retrieve()
                    .bodyToMono(Doctor.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new DoctorsNotFoundException("Doctor with ID " + userId + " not found.");
        }
    }


    //Get All Doctors
    public List<GetAllDoctor> getDoctorsInGroup(String bearerToken) {
        String groupId = "f9e44dd8-1f49-4e38-8474-25a3cbdf71e0";

        List<GetAllDoctor> doctors =  webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/admin/realms/Appointment/groups/" + groupId + "/members")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(GetAllDoctor.class)
                .collectList()
                .block();

        if (doctors == null || doctors.isEmpty()) {
            throw new DoctorsNotFoundException("No doctors found in the specified group.");
        }

        return doctors;
    }

    //Delete Doctor
    public void deleteDoctor(String userId, String bearerToken) {
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8081/admin/realms/Appointment/users/{id}", userId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new DoctorsNotFoundException("Doctor with ID " + userId + " not found.");
        }
    }


    //Update Doctor Info
    public void updateDoctor(String userId, Doctor updatedDoctor,String bearerToken) {
        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8081/admin/realms/Appointment/users/{id}",userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .body(BodyInserters.fromValue(updatedDoctor))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new DoctorsNotFoundException("Doctor with ID " + userId + " not found.");
        }
    }
}
