package com.appointment.CancelService.Service;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.CancelService.Entity.Cancel;
import com.appointment.CancelService.Exception.CancelException;
import com.appointment.CancelService.Exception.WebClientException;
import com.appointment.CancelService.Repository.CancelRepository;
import com.appointment.CancelService.Request.CancelDataForNotification;
import com.appointment.CancelService.Request.CancelRequest;
import com.appointment.CancelService.Request.UserTokenData;
import com.appointment.CancelService.Response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CancelService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    private KafkaTemplate<String,Object> template;
    private final CancelRepository cancelRepository;
    public CancelService(WebClient.Builder webClientBuilder, CancelRepository cancelRepository) {
        this.webClientBuilder = webClientBuilder;
        this.cancelRepository = cancelRepository;
    }


            /*TODO:
            -Circuit breaker
            -TimeLimiter
            */

    public MessageResponse cancelAppointment(String transactionId, UserTokenData userTokenData, String bearerToken, CancelRequest cancelRequest) throws JsonProcessingException {
        try {
            // Get the data from Appointment
            Appointment appointment = webClientBuilder.build()
                    .get()
                    .uri("http://Appointment-Service/api/appointment/transactionId/{transactionId}", transactionId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                    .retrieve()
                    .bodyToMono(Appointment.class)
                    .block();

            if (appointment != null) {
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

                    CancelDataForNotification cancelData = new CancelDataForNotification();
                    cancelData.setTransactionId(appointment.getTransactionId());
                    cancelData.setDoctorEmail(appointment.getDoctorEmail());
                    cancelData.setCancelReason(cancelRequest.getCancelReason());

                    sendMessageToTopic(cancelData,userTokenData);
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

    //TODO: Create Producer kafka

    public void sendMessageToTopic(CancelDataForNotification cancelDataForNotification,UserTokenData userTokenData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert the AppointmentMessage object to JSON
            String jsonMessage = objectMapper.writeValueAsString(cancelDataForNotification);

            CompletableFuture<SendResult<String, Object>> future = template.send("cancel", userTokenData.getSub(), jsonMessage);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Sent message with key=[{}] and value=[{}] to partition=[{}] with offset=[{}]",
                            userTokenData.getSub(), jsonMessage, metadata.partition(), metadata.offset());
                } else {
                    log.error("Unable to send message with key=[{}] and value=[{}] due to: {}", userTokenData.getSub(), jsonMessage, ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error occurred while serializing AppointmentRequest to JSON: {}", e.getMessage());
            // Handle the exception appropriately, e.g., throw it or log it.
        }
    }
    //FindAll
    public List<Cancel> getAllCancelAppointments() {
        List<Cancel> approves = cancelRepository.findAll();
        if (approves.isEmpty()) {
            throw new CancelException("No cancel appointment found.");
        }
        return approves;
    }

    //GetByTransaction
    public Cancel getCancelByTransactionId(String transactionId) {
        Optional<Cancel> cancelOptional = cancelRepository.findByTransactionId(transactionId);
        return cancelOptional.orElseThrow(() -> new CancelException("Cancel with TransactionID " + transactionId + " not found."));
    }

    //GetById
    public Cancel getCancelById(Long cancelId) {
        Optional<Cancel> cancelOptional = cancelRepository.findById(cancelId);
        return cancelOptional.orElseThrow(() -> new CancelException("Cancel with Id " + cancelId + " not found."));
    }

}
