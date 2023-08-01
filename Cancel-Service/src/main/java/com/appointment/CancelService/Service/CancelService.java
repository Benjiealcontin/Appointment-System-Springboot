package com.appointment.CancelService.Service;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.CancelService.Entity.Cancel;
import com.appointment.CancelService.Exception.CancelException;
import com.appointment.CancelService.Exception.WebClientException;
import com.appointment.CancelService.Repository.CancelRepository;
import com.appointment.CancelService.Response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Service
public class CancelService {

    private final WebClient.Builder webClientBuilder;

    private final CancelRepository cancelRepository;
    public CancelService(WebClient.Builder webClientBuilder, CancelRepository cancelRepository) {
        this.webClientBuilder = webClientBuilder;
        this.cancelRepository = cancelRepository;
    }

    public MessageResponse cancelAppointment(String transactionId, String subject, String bearerToken) throws JsonProcessingException {
        try {

            /*TODO:
            -Circuit breaker
            -TimeLimiter
            */

            // Get the data from Appointment
            Appointment appointment = webClientBuilder.build()
                    .get()
                    .uri("http://Appointment-Service/api/appointment/transactionId/{transactionId}", transactionId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                    .retrieve()
                    .bodyToMono(Appointment.class)
                    .block();

            if (appointment != null) {
                if(Objects.equals(subject, appointment.getPatientId())){
                    Cancel cancel = new Cancel();
                    cancel.setAppointmentReason(appointment.getAppointmentReason());
                    cancel.setAppointmentType(appointment.getAppointmentType());
                    cancel.setDoctorId(appointment.getDoctorId());
                    cancel.setAppointmentStatus("Cancelled");
                    cancel.setPatientId(appointment.getPatientId());
                    cancel.setDateField(appointment.getDateField());
                    cancel.setTimeField(appointment.getTimeField());
                    cancel.setTransactionId(appointment.getTransactionId());
                    cancelRepository.save(cancel);

                    //Delete after save the data from Appointment
                    webClientBuilder.build()
                            .delete()
                            .uri("http://Appointment-Service/api/appointment/delete/{appointmentId}", appointment.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                            .retrieve()
                            .bodyToMono(Appointment.class)
                            .block();
                }else {
                    // Throw an exception if the appointment object is null
                    throw new IllegalArgumentException("The patientId and patientId from the database are not equal.");
                }
            } else {
                // Throw an exception if the appointment object is null
                throw new IllegalArgumentException("The appointment object is null.");
            }
            return new MessageResponse("Cancel Successfully");

        } catch (WebClientResponseException.NotFound ex) {
            // Parse the error response to get the message
            String responseBody = ex.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Rethrow the AppointmentNotFoundException from the ApprovalService
            throw new AppointmentNotFoundException(jsonNode.get("message").asText());
        } catch (WebClientResponseException.ServiceUnavailable ex) {
            // Handle the service unavailable case appropriately
            throw new WebClientException("Error occurred while calling the external service: " + ex.getMessage());
        } catch (Exception e) {
            throw new CancelException("Error occurred while canceling the appointment." + e.getMessage()); // Return an appropriate response in case of an error
        }
    }


}
