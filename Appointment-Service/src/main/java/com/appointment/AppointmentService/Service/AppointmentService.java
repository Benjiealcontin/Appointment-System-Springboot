package com.appointment.AppointmentService.Service;

import com.appoinment.DoctorService.Entity.Doctors;
import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AddAppointmentException;
import com.appointment.AppointmentService.Exception.AppointmentHoursMismatchException;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.AppointmentService.Exception.WebClientException;
import com.appointment.AppointmentService.Repository.AppointmentRepository;
import com.appointment.AppointmentService.Request.AppointmentData;
import com.appointment.AppointmentService.Request.AppointmentMessage;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Request.UserTokenData;
import com.appointment.AppointmentService.Response.MessageResponse;
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

    //TODO: Circuit Beaker and time limiter

    //Add Appointment
    public MessageResponse createAppointment(AppointmentRequest appointmentRequest, String subject,String bearerToken,UserTokenData userTokenData) throws JsonProcessingException {
        try {
            Doctors doctor = webClientBuilder.build()
                    .get()
                    .uri("http://Doctor-Service/api/doctor/getDoctorById/{doctorId}", appointmentRequest.getDoctorId())
                    .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                    .retrieve()
                    .bodyToMono(Doctors.class)
                    .block();

            if (doctor != null) {
                List<String> doctorWorkingHours = doctor.getWorkingHours();

                // Compare appointment time to doctorWorkingHours
                if (!doctorWorkingHours.contains(appointmentRequest.getTimeField())) {
                    throw new AppointmentHoursMismatchException("Appointment time does not match doctor's working hours.");
                }

                Appointment appointment = new Appointment();
                appointment.setAppointmentReason(appointmentRequest.getAppointmentReason());
                appointment.setTransactionId(appointmentRequest.getTransactionId());
                appointment.setDoctorName(doctor.getDoctorName());
                appointment.setLocation(doctor.getLocation());
                appointment.setAppointmentType(appointmentRequest.getAppointmentType());
                appointment.setAppointmentStatus("Pending");
                appointment.setDoctorId(appointmentRequest.getDoctorId());
                appointment.setPatientId(subject);
                appointment.setDateField(appointmentRequest.getDateField());
                appointment.setTimeField(appointmentRequest.getTimeField());

                AppointmentData appointmentData = new AppointmentData();
                appointmentData.setAppointmentStatus(appointmentRequest.getAppointmentStatus());
                appointmentData.setAppointmentReason(appointmentRequest.getAppointmentReason());
                appointmentData.setAppointmentType(appointmentRequest.getAppointmentType());
                appointmentData.setTransactionId(appointmentRequest.getTransactionId());
                appointmentData.setDoctorName(doctor.getDoctorName());
                appointmentData.setDateField(appointmentRequest.getDateField());
                appointmentData.setTimeField(appointmentRequest.getTimeField());
                // Send message to topic
                sendMessageToTopic(appointmentData, userTokenData);

                appointmentRepository.save(appointment);
            }else{
                // Throw an exception if the appointment object is null
                throw new IllegalArgumentException("The doctor object is null.");
            }

            return new MessageResponse("Appointment Successfully!");
        } catch (WebClientResponseException.NotFound ex) {
            // Parse the error response to get the message
            String responseBody = ex.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Rethrow the AppointmentNotFoundException from the ApprovalService
            throw new AppointmentNotFoundException(jsonNode.get("message").asText());
        } catch (WebClientResponseException.ServiceUnavailable ex) {
            throw new WebClientException("Error occurred while calling the external service: Service Unavailable from Appointment Service");
        } catch (AppointmentHoursMismatchException e) {
            throw e;
        }catch (Exception e){
            throw new AddAppointmentException("Error occurred while appointing the appointment." + e.getMessage());
        }
    }

    //Send notification
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
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            // Update the appointment with the new values from the appointment request
            appointment.setDoctorId(appointmentRequest.getDoctorId());
            appointment.setAppointmentReason(appointmentRequest.getAppointmentReason());
            appointment.setAppointmentType(appointmentRequest.getAppointmentType());
            appointment.setAppointmentStatus(appointmentRequest.getAppointmentStatus());
            appointment.setTransactionId(appointmentRequest.getTransactionId());

            // Save the updated appointment
            appointmentRepository.save(appointment);
        } else {
            throw new AppointmentNotFoundException("Appointment with ID " + appointmentId + " not found.");
        }
    }
}