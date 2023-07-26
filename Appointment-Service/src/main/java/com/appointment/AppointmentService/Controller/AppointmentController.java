package com.appointment.AppointmentService.Controller;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AddAppointmentException;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Response.MessageResponse;
import com.appointment.AppointmentService.Service.AppointmentService;
import com.appointment.AppointmentService.Service.TokenDecodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest appointmentRequest,@RequestHeader("Authorization") String bearerToken){
        try {
            String token = tokenService.extractToken(bearerToken);
            String subject = tokenService.decodeSubjectFromToken(token);
            appointmentService.sendMessageToTopic(appointmentRequest,subject);
            return ResponseEntity.ok(appointmentService.createAppointment(appointmentRequest,subject));
        } catch (AddAppointmentException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll
    @GetMapping("/getAllAppointment")
    private ResponseEntity<?> getAllDoctors() {
        try {
            return ResponseEntity.ok(appointmentService.getAllAppointment());
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    //FindById
    @GetMapping("/{appointmentId}")
    private ResponseEntity<?> getAppointmentId(@PathVariable Long appointmentId) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentId(appointmentId));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //FindByTransactionId
    @GetMapping("/transactionId/{transactionId}")
    private ResponseEntity<?> getByTransactionId(@PathVariable String transactionId) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentByTransactionId(transactionId));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }
    //Delete Appointment
    @DeleteMapping("/delete/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            return ResponseEntity.ok(appointmentService.deleteAppointment(appointmentId));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //Update Appointment Info
    @PutMapping("/update/{appointmentId}")
    private ResponseEntity updateAppointment(@PathVariable long appointmentId, @RequestBody AppointmentRequest appointmentRequest) {
        try{
            return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, appointmentRequest));
        }catch(AppointmentNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

}
