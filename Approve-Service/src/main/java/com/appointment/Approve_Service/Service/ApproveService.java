package com.appointment.Approve_Service.Service;


import com.appoinment.DoctorService.Entity.Doctors;
import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.Approve_Service.Entity.Approve;
import com.appointment.Approve_Service.Exception.ApprovalException;
import com.appointment.Approve_Service.Exception.ApproveNotFoundException;
import com.appointment.Approve_Service.Exception.WebClientException;
import com.appointment.Approve_Service.Repository.ApproveRepository;
import com.appointment.Approve_Service.Request.*;
import com.appointment.Approve_Service.Response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.HttpHeaders;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ApproveService {

    private final ApproveRepository approveRepository;

    @Autowired
    private KafkaTemplate<String,Object> template;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ApproveService(ApproveRepository approveRepository, WebClient.Builder webClientBuilder) {
        this.approveRepository = approveRepository;
        this.webClientBuilder = webClientBuilder;
    }



    //Approve Appointment
    @CircuitBreaker(name = "approveAppointment", fallbackMethod = "ApproveMethodFallBack")
    public MessageResponse approveAppointment(String transactionId, String bearerToken, UserTokenData userTokenData) throws JsonProcessingException {
       try{
           //Get the data from Appointment
           Appointment appointment = webClientBuilder.build()
                   .get()
                   .uri("http://Appointment-Service/api/appointment/transactionId/{transactionId}", transactionId)
                   .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                   .retrieve()
                   .bodyToMono(Appointment.class)
                   .block();

           //Save the Approve data
           if (appointment != null) {
               Approve approve = new Approve();
               approve.setAppointmentReason(appointment.getAppointmentReason());
               approve.setAppointmentType(appointment.getAppointmentType());
               approve.setDoctorId(appointment.getDoctorId());
               approve.setAppointmentStatus("Approved");
               approve.setPatientId(appointment.getPatientId());
               approve.setDateField(appointment.getDateField());
               approve.setTimeField(appointment.getTimeField());
               approve.setTransactionId(appointment.getTransactionId());
               approveRepository.save(approve);

               //Delete after save the data from Appointment
               webClientBuilder.build()
                       .delete()
                       .uri("http://Appointment-Service/api/appointment/delete/{appointmentId}", appointment.getId())
                       .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                       .retrieve()
                       .bodyToMono(Appointment.class)
                       .block();

               //Get the data from doctor service
               Doctors doctors = webClientBuilder.build()
                       .get()
                       .uri("http://Doctor-Service/api/doctor/getDoctorById/{doctorId}", appointment.getDoctorId())
                       .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                       .retrieve()
                       .bodyToMono(Doctors.class)
                       .block();

               if (doctors != null) {
                   ApproveData approveData = new ApproveData();
                   approveData.setAppointmentReason(appointment.getAppointmentReason());
                   approveData.setLocation(doctors.getLocation());
                   approveData.setDoctorName(doctors.getDoctorName());
                   approveData.setTransactionId(appointment.getTransactionId());
                   approveData.setTimeField(appointment.getTimeField());
                   approveData.setDateField(appointment.getDateField());
                   approveData.setAppointmentType(appointment.getAppointmentType());
                   approveData.setPatientEmail(appointment.getPatientEmail());

                   sendMessageToTopicForApproved(approveData,userTokenData);
               }
           } else {
               // Throw an exception if the appointment object is null
               throw new IllegalArgumentException("The appointment object is null.");
           }

           return new MessageResponse("Appointment Approved Successfully");
       } catch (WebClientResponseException.NotFound ex) {
           // Parse the error response to get the message
           String responseBody = ex.getResponseBodyAsString();
           ObjectMapper objectMapper = new ObjectMapper();
           JsonNode jsonNode = objectMapper.readTree(responseBody);

           // Rethrow the AppointmentNotFoundException from the ApprovalService
           throw new ApproveNotFoundException(jsonNode.get("message").asText());
       } catch (WebClientResponseException.ServiceUnavailable ex) {
           throw new WebClientException("Error occurred while calling the external service: ","Service Unavailable from Appointment Service");
       }catch (Exception e){
           throw new ApprovalException("Error occurred while approving the appointment." + e.getMessage());
       }
    }

    public MessageResponse approveMethodFallback(String transactionId, String bearerToken, UserTokenData userTokenData, Throwable t) {
        log.warn("Circuit breaker fallback: Unable to approve appointment. Error: {}", t.getMessage());
        return new MessageResponse("Appointment approval is currently unavailable. Please try again later.");
    }

    //Send notification
    @CircuitBreaker(name = "sendMessageToTopicForApproved", fallbackMethod = "sendMessageToTopicForApproved")
    public void sendMessageToTopicForApproved(ApproveData approveData, UserTokenData userTokenData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert the AppointmentMessage object to JSON
            String jsonMessage = objectMapper.writeValueAsString(approveData);

            CompletableFuture<SendResult<String, Object>> future = template.send("approve", userTokenData.getSub(), jsonMessage);
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

    public void sendMessageToTopicForApprovedFallback(ApproveData approveData, UserTokenData userTokenData, Throwable t) {
        log.warn("Circuit breaker fallback: Unable to send approved message to topic. Error: {}", t.getMessage());
    }

    @CircuitBreaker(name = "disapproveAppointment", fallbackMethod = "DisapproveMethodFallBack")
    public MessageResponse disapproveAppointment(String transactionId, String bearerToken, UserTokenData userTokenData, DisapproveRequest disapproveRequest) throws JsonProcessingException {
        try{
            //Get the data from Appointment
            Appointment appointment = webClientBuilder.build()
                    .get()
                    .uri("http://Appointment-Service/api/appointment/transactionId/{transactionId}", transactionId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                    .retrieve()
                    .bodyToMono(Appointment.class)
                    .block();

            //Save the Approve data
            if (appointment != null) {
                Approve approve = new Approve();
                approve.setAppointmentReason(appointment.getAppointmentReason());
                approve.setAppointmentType(appointment.getAppointmentType());
                approve.setDoctorId(appointment.getDoctorId());
                approve.setAppointmentStatus("Disapproved");
                approve.setPatientId(appointment.getPatientId());
                approve.setDateField(appointment.getDateField());
                approve.setTimeField(appointment.getTimeField());
                approve.setTransactionId(appointment.getTransactionId());
                approveRepository.save(approve);

                //Delete after save the data from Appointment
                webClientBuilder.build()
                        .delete()
                        .uri("http://Appointment-Service/api/appointment/delete/{appointmentId}", appointment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                        .retrieve()
                        .bodyToMono(Appointment.class)
                        .block();

                //Get the data from doctor service
                Doctors doctors = webClientBuilder.build()
                        .get()
                        .uri("http://Doctor-Service/api/doctor/getDoctorById/{doctorId}", appointment.getDoctorId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                        .retrieve()
                        .bodyToMono(Doctors.class)
                        .block();

                if (doctors != null) {
                    DisapproveData disapproveData = new DisapproveData();
                    disapproveData.setTransactionId(appointment.getTransactionId());
                    disapproveData.setDisapproveReason(disapproveRequest.getDisapproveReason());
                    disapproveData.setPatientEmail(appointment.getPatientEmail());
                    sendMessageToTopicForDisApproved(disapproveData,userTokenData);
                }
            } else {
                // Throw an exception if the appointment object is null
                throw new IllegalArgumentException("The appointment object is null.");
            }

            return new MessageResponse("Appointment Disapproved is Successfully");
        } catch (WebClientResponseException.NotFound ex) {
            // Parse the error response to get the message
            String responseBody = ex.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Rethrow the AppointmentNotFoundException from the ApprovalService
            throw new ApproveNotFoundException(jsonNode.get("message").asText());
        } catch (WebClientResponseException.ServiceUnavailable ex) {
            throw new WebClientException("Error occurred while calling the external service: ","Service Unavailable from Appointment Service");
        }catch (Exception e){
            throw new ApprovalException("Error occurred while approving the appointment." + e.getMessage());
        }
    }

    public MessageResponse DisapproveMethodFallBack(String transactionId, String bearerToken, UserTokenData userTokenData, Throwable t) {
        log.warn("Circuit breaker fallback: Unable to disapprove appointment. Error: {}", t.getMessage());
        return new MessageResponse("Appointment disapproval is currently unavailable. Please try again later.");
    }


    //Send notification
    @CircuitBreaker(name = "sendMessageToTopicForDisApproved", fallbackMethod = "sendMessageToTopicForDisApprovedFallBackMethod")
    public void sendMessageToTopicForDisApproved(DisapproveData disapproveData, UserTokenData userTokenData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert the AppointmentMessage object to JSON
            String jsonMessage = objectMapper.writeValueAsString(disapproveData);

            CompletableFuture<SendResult<String, Object>> future = template.send("disapprove", userTokenData.getSub(), jsonMessage);
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

    public void sendMessageToTopicForDisApprovedFallBackMethod(ApproveData approveData, UserTokenData userTokenData, Throwable t) {
        log.warn("Circuit breaker fallback: Unable to send approved message to topic. Error: {}", t.getMessage());
    }

    //FindAll Approve
    public List<Approve> getAllApproveAppointments() {
        List<Approve> approves = approveRepository.findAll();
        if (approves.isEmpty()) {
            throw new ApproveNotFoundException("No approves found.");
        }
        return approves;
    }

    //FindAllApprovedRequestOfDoctor
    public List<Approve> getAllApproveAppointmentByDoctorId(Long doctorId){
        List<Approve> approveList = approveRepository.findByDoctorIdAndAppointmentStatus(doctorId,"Approved");
        if (approveList.isEmpty()) {
            throw new ApproveNotFoundException("Doctor with ID " + doctorId + " no approved data.");
        }
        return approveList;
    }

    //FindAllDisapprovedRequestOfDoctor
    public List<Approve> getAllDisapproveAppointmentByDoctorId(Long doctorId){
        List<Approve> disapproveList = approveRepository.findByDoctorIdAndAppointmentStatus(doctorId,"Disapproved");
        if (disapproveList.isEmpty()) {
            throw new ApproveNotFoundException("Doctor with ID " + doctorId + " no disapproved data.");
        }
        return disapproveList;
    }

    //FindById
    public Approve getApproveById(Long approveId) {
        Optional<Approve> approveOptional = approveRepository.findById(approveId);
        return approveOptional.orElseThrow(() -> new ApproveNotFoundException("Approve with ID " + approveId + " not found."));
    }


    //Delete approve
    public void deleteApproveById(Long approveId) {
        if (!approveRepository.existsById(approveId)) {
            throw new ApproveNotFoundException("Approve with ID " + approveId + " not found.");
        }
        approveRepository.deleteById(approveId);
    }

    //Update Approve
    public void updateApproveById(Long approveId, ApproveRequest approveRequest) {
        Optional<Approve> optionalApprove = approveRepository.findById(approveId);
        if (optionalApprove.isPresent()) {
            Approve approve = optionalApprove.get();
            approve.setDoctorId(approveRequest.getDoctorId());
            approve.setAppointmentStatus(approveRequest.getAppointmentStatus());
            approve.setAppointmentType(approveRequest.getAppointmentType());
            approve.setAppointmentReason(approveRequest.getAppointmentReason());
            approve.setPatientId(approveRequest.getPatientId());
            approve.setDateField(approveRequest.getDateField());
            approve.setTimeField(approveRequest.getTimeField());
            approve.setTransactionId(approveRequest.getTransactionId());

            approveRepository.save(approve);
        } else {
            throw new ApproveNotFoundException("Approve with ID " + approveId + " not found.");
        }
    }
}
