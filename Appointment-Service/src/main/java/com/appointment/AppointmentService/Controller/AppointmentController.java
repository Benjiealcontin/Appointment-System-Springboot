package com.appointment.AppointmentService.Controller;

import com.appoinment.DoctorService.Response.ErrorResponse;
import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.*;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import com.appointment.AppointmentService.Request.UserTokenData;
import com.appointment.AppointmentService.Response.MessageResponse;
import com.appointment.AppointmentService.Service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    private final TokenDecodeService tokenService;

    private final CheckIfExistService checkIfExistService;

    public AppointmentController(AppointmentService appointmentService, TokenDecodeService tokenService, CheckIfExistService checkIfExistService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
        this.checkIfExistService = checkIfExistService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest,
                                               @RequestHeader("Authorization") String bearerToken,
                                               BindingResult bindingResult) {
        //Handle the Errors of validation
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        try {

            String token = tokenService.extractToken(bearerToken);
            UserTokenData userTokenData = tokenService.decodeUserToken(token,bearerToken);

            boolean isPast = DateUtils.isDateInPast(appointmentRequest.getDateField());
            if (isPast) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot book an appointment in the past. Please choose a date in the present or future.");
            } else {
                //Check if Exist
                boolean exists = checkIfExistService.doesDataExist(appointmentRequest);
                if (exists) {
                    String message = "Appointment is already booked on " + appointmentRequest.getDateField() + " at " + appointmentRequest.getTimeField() +
                            ". Please choose another date and time.";
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
                } else {
                    // Create the appointment
                    return ResponseEntity.ok(appointmentService.createAppointment(appointmentRequest,bearerToken,userTokenData));
                }
            }
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AppointmentHoursMismatchException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (WebClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
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
    @GetMapping("/getById/{appointmentId}")
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

    //GetAllPendingAppointmentOfDoctorByDoctorId
    @GetMapping("/getByDoctorId/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable String doctorId) {
        try {
            List<Appointment> appointmentList =  appointmentService.getAppointmentsByDoctorId(doctorId);
            return ResponseEntity.ok(appointmentList);
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