package com.appointment.AppointmentService.Service;


import com.appoinment.DoctorService.Request.Doctor;
import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.*;
import com.appointment.AppointmentService.Repository.AppointmentRepository;
import com.appointment.AppointmentService.Request.AppointmentData;
import com.appointment.AppointmentService.Request.AppointmentMessage;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Request.UserTokenData;
import com.appointment.AppointmentService.Response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    private KafkaTemplate<String,Object> template;

    private final WebClient.Builder webClientBuilder;

    public AppointmentService(AppointmentRepository appointmentRepository, WebClient.Builder webClientBuilder) {
        this.appointmentRepository = appointmentRepository;
        this.webClientBuilder = webClientBuilder;
    }


    //Add Appointment
    @CircuitBreaker(name = "addAppointment", fallbackMethod = "AppointmentMethodFallBack")
    public MessageResponse createAppointment(AppointmentRequest appointmentRequest,String bearerToken,UserTokenData userTokenData) throws JsonProcessingException {
        try {
            Doctor doctor = webClientBuilder.build()
                    .get()
                    .uri("http://Doctor-Service/api/doctor/getDoctorById/{doctorId}", appointmentRequest.getDoctorId())
                    .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                    .retrieve()
                    .bodyToMono(Doctor.class)
                    .block();

            //Get the data from the attributes from keycloak
            assert doctor != null;
            Map<String, Object> doctorAttributes = doctor.getAttributes();
            Object locationValue = doctorAttributes.get("clinicAddress");
            String Address = locationValue.toString();
            String clinicAddress = Address.replaceAll("[\\[\\]]", "");

            Appointment appointment = Appointment.builder()
                    .doctorEmail(doctor.getEmail())
                    .patientEmail(userTokenData.getEmail())
                    .doctorName(doctor.getFullName())
                    .location(clinicAddress)
                    .appointmentReason(appointmentRequest.getAppointmentReason())
                    .transactionId(appointmentRequest.getTransactionId())
                    .appointmentType(appointmentRequest.getAppointmentType())
                    .appointmentStatus("Pending")
                    .doctorId(appointmentRequest.getDoctorId())
                    .patientId(userTokenData.getSub())
                    .dateField(appointmentRequest.getDateField())
                    .timeField(appointmentRequest.getTimeField())
                    .build();

            AppointmentData appointmentData = AppointmentData.builder()
                    .appointmentStatus("Pending")
                    .doctorEmail(doctor.getEmail())
                    .doctorName(doctor.getFullName())
                    .appointmentReason(appointmentRequest.getAppointmentReason())
                    .appointmentType(appointmentRequest.getAppointmentType())
                    .transactionId(appointmentRequest.getTransactionId())
                    .dateField(appointmentRequest.getDateField())
                    .timeField(appointmentRequest.getTimeField())
                    .build();

            // Send message to topic
            sendMessageToTopic(appointmentData, userTokenData);

            appointmentRepository.save(appointment);

            return new MessageResponse("Appointment Successfully!");
        } catch (WebClientResponseException.NotFound ex) {
            throw new DoctorNotFoundException("Doctor not found in the Doctor service.");
        } catch (WebClientResponseException.ServiceUnavailable ex) {
            throw new WebClientException("Error occurred while calling the external service: Service Unavailable from other Service");
        } catch (AppointmentHoursMismatchException e) {
            throw e;
        }catch (Exception e){
            throw new AddAppointmentException("Error occurred while appointing the appointment." + e.getMessage());
        }
    }

    private MessageResponse AppointmentMethodFallBack(AppointmentRequest appointmentRequest,String bearerToken,UserTokenData userTokenData,Throwable throwable) {
        log.warn("Circuit breaker fallback: Unable to create appointment. Error: {}", throwable.getMessage());
        return new MessageResponse("Appointment creation is currently unavailable. Please try again later.");
    }

    //Send notification
    @CircuitBreaker(name = "notification", fallbackMethod = "kafkaFallBackMethod")
    public void sendMessageToTopic(AppointmentData appointmentData, UserTokenData userTokenData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Create the AppointmentMessage object
            AppointmentMessage appointmentMessage = new AppointmentMessage(userTokenData, appointmentData);

            // Convert the AppointmentMessage object to JSON
            String jsonMessage = objectMapper.writeValueAsString(appointmentMessage);

            CompletableFuture<SendResult<String, Object>> future = template.send("appointment", userTokenData.getSub(), jsonMessage);
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

    public void kafkaFallBackMethod(AppointmentData appointmentData, UserTokenData userTokenData) {
        log.warn("Circuit breaker fallback: Unable to send message to Kafka topic.");
    }

    //FindAll
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("No appointments found.");
        }
        return appointments;
    }

    //FindById
    public Appointment getAppointmentById(Long appointmentId) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        return appointmentOptional.orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found."));
    }

    //FindByDoctorId
    public List<Appointment> getAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorId(doctorId);
        if (appointmentList.isEmpty()) {
            throw new AppointmentNotFoundException("Doctor with ID " + doctorId + " no appointment data.");
        }
        return appointmentList;
    }

    //FindByTransactionId
    public Appointment getAppointmentByTransactionId(String transactionId) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findByTransactionId(transactionId);
        return appointmentOptional.orElseThrow(() -> new AppointmentNotFoundException("Appointment with Transaction ID " + transactionId + " not found."));
    }

    //Delete Appointment
    public void deleteAppointment(Long appointmentId) {
        // Check if the appointment exists in the database
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found.");
        }
        // Delete the appointment from the database
        appointmentRepository.deleteById(appointmentId);
    }

    //Update the appointment
    public void updateAppointment(long appointmentId, AppointmentRequest appointmentRequest) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found."));

        appointment.setDoctorId(appointmentRequest.getDoctorId());
        appointment.setAppointmentReason(appointmentRequest.getAppointmentReason());
        appointment.setAppointmentType(appointmentRequest.getAppointmentType());
        appointment.setTransactionId(appointmentRequest.getTransactionId());

        appointmentRepository.save(appointment);
    }
}