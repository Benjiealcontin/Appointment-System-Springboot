package com.appointment.AppointmentService.Controller;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AddAppointmentException;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.AppointmentService.Exception.InvalidTokenException;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Response.MessageResponse;
import com.appointment.AppointmentService.Service.AppointmentService;
import com.appointment.AppointmentService.Service.TokenDecodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/appointment")
public class AppointmentController {


    private final AppointmentService appointmentService;


    private final TokenDecodeService tokenService;


    public AppointmentController(AppointmentService appointmentService, TokenDecodeService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest appointmentRequest, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = tokenService.extractToken(bearerToken);
            String subject = tokenService.decodeSubjectFromToken(token);

            // Send message to topic
            appointmentService.sendMessageToTopic(appointmentRequest, subject);

            // Create the appointment
            Appointment appointment = appointmentService.createAppointment(appointmentRequest, subject);
            return ResponseEntity.ok(appointment);
        } catch (AddAppointmentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll
    @GetMapping("/getAllAppointment")
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindById
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentId(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindByTransactionId
    @GetMapping("/transactionId/{transactionId}")
    public ResponseEntity<?> getByTransactionId(@PathVariable String transactionId) {
        try {
            Appointment appointment = appointmentService.getAppointmentByTransactionId(transactionId);
            return ResponseEntity.ok(appointment);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Delete Appointment
    @DeleteMapping("/delete/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.ok(new MessageResponse("Delete Successfully!"));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Update Appointment Info
    @PutMapping("/update/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@PathVariable long appointmentId, @RequestBody AppointmentRequest appointmentRequest) {
        try {
            appointmentService.updateAppointment(appointmentId, appointmentRequest);
            return ResponseEntity.ok(new MessageResponse("Appointment Details updated successfully."));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


}
