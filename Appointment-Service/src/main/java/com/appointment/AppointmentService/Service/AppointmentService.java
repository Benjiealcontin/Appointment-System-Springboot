package com.appointment.AppointmentService.Service;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AddAppointmentException;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.AppointmentService.Repository.AppointmentRepository;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Response.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
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

    public void sendMessageToTopic(AppointmentRequest appointmentRequest, String subject) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Convert the AppointmentRequest object to JSON
        String jsonMessage = objectMapper.writeValueAsString(appointmentRequest);

        CompletableFuture<SendResult<String, Object>> future = template.send("appointment", subject, jsonMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                System.out.println("Sent message with key=[" + subject +
                        "] and value=[" + jsonMessage +
                        "] to partition=[" + metadata.partition() +
                        "] with offset=[" + metadata.offset() + "]");
            } else {
                System.out.println("Unable to send message with key=[" + subject +
                        "] and value=[" + jsonMessage +
                        "] due to : " + ex.getMessage());
            }
        });
    }

    //FindAll
    public Iterable<Appointment> getAllAppointment() {
        Iterable<Appointment> appointment = appointmentRepository.findAll();
        if (!appointment.iterator().hasNext()) {
            throw new AppointmentNotFoundException("No appointment found.");
        }
        return appointment;
    }

    //FindById
    public Appointment getAppointmentId(Long AppointmentId) {
        Optional<Appointment> doctorOptional = appointmentRepository.findById(AppointmentId);
        return doctorOptional.orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + AppointmentId + " not found."));
    }

    //FindByTransactionId
    public Appointment getAppointmentByTransactionId(String transactionId) {
        Optional<Appointment> doctorOptional = appointmentRepository.findByTransactionId(transactionId);
        return doctorOptional.orElseThrow(() -> new AppointmentNotFoundException("Appointment with Transaction ID " + transactionId + " not found."));
    }

    //Delete Appointment
    public MessageResponse deleteAppointment(Long AppointmentId) {
        // Check if the doctor exists in the database
        if (!appointmentRepository.existsById(AppointmentId)) {
            throw new AppointmentNotFoundException("Appointment with ID " + AppointmentId + " not found.");
        }
        // Delete the doctor from the database
        appointmentRepository.deleteById(AppointmentId);
        return new MessageResponse("Appointment deleted successfully.");
    }

    //Update the appointment
    public MessageResponse updateAppointment(long id, AppointmentRequest appointmentRequest) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
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

            // Return the success response
            return new MessageResponse("Appointment Updated Successfully");
        } else {
            throw new AppointmentNotFoundException("Appointment with ID " + id + " not found.");
        }
    }

}
