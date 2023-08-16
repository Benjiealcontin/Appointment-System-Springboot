package com.appointment.Approve_Service.Controller;

import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.Approve_Service.Entity.Approve;
import com.appointment.Approve_Service.Exception.ApproveNotFoundException;
import com.appointment.Approve_Service.Exception.WebClientException;
import com.appointment.Approve_Service.Request.ApproveRequest;
import com.appointment.Approve_Service.Request.DisapproveRequest;
import com.appointment.Approve_Service.Request.UserTokenData;
import com.appointment.Approve_Service.Response.MessageResponse;
import com.appointment.Approve_Service.Service.ApproveService;
import com.appointment.Approve_Service.Service.TokenDecodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/approve")
public class ApproveController {


    private final ApproveService approveService;

    private final TokenDecodeService tokenService;

    public ApproveController(ApproveService approveService, TokenDecodeService tokenDecodeService) {
        this.approveService = approveService;
        this.tokenService = tokenDecodeService;
    }


    //Approve Appointment
    @PostMapping("/{transactionId}")
    public ResponseEntity<?> approveAppointment(@PathVariable String transactionId, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = tokenService.extractToken(bearerToken);
            UserTokenData userTokenData = tokenService.decodeUserToken(token);

            MessageResponse response = approveService.approveAppointment(transactionId, bearerToken, userTokenData);
            return ResponseEntity.ok(response);
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (WebClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/disapprove/{transactionId}")
    public ResponseEntity<?> disapproveAppointment(@RequestBody DisapproveRequest disapproveRequest, @PathVariable String transactionId, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = tokenService.extractToken(bearerToken);
            UserTokenData userTokenData = tokenService.decodeUserToken(token);

            MessageResponse response = approveService.disapproveAppointment(transactionId, bearerToken, userTokenData, disapproveRequest);
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


    //FindByTransactionId
    @GetMapping("/findByTransactionId/{transactionId}")
    public ResponseEntity<?> getApproveByTransactionId(@PathVariable String transactionId){
        try {
            Approve approve = approveService.getApproveByTransactionId(transactionId);
            return ResponseEntity.ok().body(approve);
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAllApprovedRequestOfDoctor
    @GetMapping("/approved/{doctorId}")
    public ResponseEntity<?> getAllApproveOfDoctor(@PathVariable String doctorId) {
        try {
            List<Approve> approveList = approveService.getAllApproveAppointmentByDoctorId(doctorId);
            return ResponseEntity.ok(approveList);
        } catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAllDisapprovedRequestOfDoctor
    @GetMapping("/disapproved/{doctorId}")
    public ResponseEntity<?> getAllDisapproveOfDoctor(@PathVariable String doctorId) {
        try {
            List<Approve> disapproveList = approveService.getAllDisapproveAppointmentByDoctorId(doctorId);
            return ResponseEntity.ok(disapproveList);
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

    //Update Approve details
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
