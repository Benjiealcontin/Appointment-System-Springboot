package com.appointment.AppointmentService.Service;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AddAppointmentException;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.AppointmentService.Repository.AppointmentRepository;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    private KafkaTemplate<String,Object> template;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    //Add Appointment
    public Appointment createAppointment(AppointmentRequest appointmentRequest, String subject) {
        try {
            Appointment appointment = new Appointment();
            appointment.setAppointmentReason(appointmentRequest.getAppointmentReason());
            appointment.setTransactionId(appointmentRequest.getTransactionId());
            appointment.setDoctorName(appointmentRequest.getDoctorName());
            appointment.setLocation(appointmentRequest.getLocation());
            appointment.setAppointmentType(appointmentRequest.getAppointmentType());
            appointment.setAppointmentStatus("Pending");
            appointment.setEmail(appointmentRequest.getEmail());
            appointment.setDoctorId(appointmentRequest.getDoctorId());
            appointment.setPatientId(subject);
            appointment.setDateField(appointmentRequest.getDateField());
            appointment.setTimeField(appointmentRequest.getTimeField());

            return appointmentRepository.save(appointment);
        } catch (DataAccessException e) {
            // Log the error or perform any other actions as needed
            throw new AddAppointmentException("Error occurred while saving the appointment.", e);
        }
    }

    public void sendMessageToTopic(AppointmentRequest appointmentRequest, String subject) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Convert the AppointmentRequest object to JSON
            String jsonMessage = objectMapper.writeValueAsString(appointmentRequest);

            CompletableFuture<SendResult<String, Object>> future = template.send("appointment", subject, jsonMessage);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Sent message with key=[{}] and value=[{}] to partition=[{}] with offset=[{}]",
                            subject, jsonMessage, metadata.partition(), metadata.offset());
                } else {
                    log.error("Unable to send message with key=[{}] and value=[{}] due to: {}", subject, jsonMessage, ex.getMessage());
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
