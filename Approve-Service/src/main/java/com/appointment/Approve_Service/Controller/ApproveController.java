package com.appointment.Approve_Service.Controller;

import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.Approve_Service.Entity.Approve;
import com.appointment.Approve_Service.Exception.ApproveNotFoundException;
import com.appointment.Approve_Service.Exception.WebClientException;
import com.appointment.Approve_Service.Request.ApproveRequest;
import com.appointment.Approve_Service.Response.MessageResponse;
import com.appointment.Approve_Service.Service.ApproveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/approve")
public class ApproveController {


    private final ApproveService approveService;

    public ApproveController(ApproveService approveService) {
        this.approveService = approveService;
    }


    //Approve Appointment
    @PostMapping("/{transactionId}")
    public ResponseEntity<?> approveAppointment(@PathVariable String transactionId, @RequestHeader("Authorization") String token) {
        try {
            MessageResponse response = approveService.approveAppointment(transactionId, token);
            return ResponseEntity.ok(response);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (WebClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll Approve Appointment
    @GetMapping("/getAllApprove")
    public ResponseEntity<?> getAllApprove() {
        try {
            List<Approve> approves = approveService.getAllApproveAppointments();
            return ResponseEntity.ok(approves);
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindById
    @GetMapping("/findById/{approveId}")
    public ResponseEntity<?> getApproveById(@PathVariable Long approveId) {
        try {
            Approve approve = approveService.getApproveById(approveId);
            return ResponseEntity.ok().body(approve);
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    //Delete by Id
    @DeleteMapping("/delete/{approveId}")
    public ResponseEntity<?> deleteApproveById(@PathVariable Long approveId) {
        try {
            approveService.deleteApproveById(approveId);
            return ResponseEntity.ok(new MessageResponse("Delete Successfully!"));
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/update/{approveId}")
    public ResponseEntity<?> updateApprove(@PathVariable Long approveId, @RequestBody ApproveRequest approveRequest) {
        try {
            approveService.updateApproveById(approveId, approveRequest);
            return ResponseEntity.ok(new MessageResponse("Approve Details updated successfully."));
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
