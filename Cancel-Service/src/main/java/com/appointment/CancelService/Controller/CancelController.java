package com.appointment.CancelService.Controller;

import com.appointment.CancelService.Entity.Cancel;
import com.appointment.CancelService.Exception.CancelException;
import com.appointment.CancelService.Exception.CancelNotFoundException;
import com.appointment.CancelService.Request.CancelRequest;
import com.appointment.CancelService.Request.UpdateCancelDetails;
import com.appointment.CancelService.Request.UserTokenData;
import com.appointment.CancelService.Response.MessageResponse;
import com.appointment.CancelService.Service.CancelService;
import com.appointment.CancelService.Service.TokenDecodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@RestController
@RequestMapping("/api/cancel")
public class CancelController {

    private final CancelService cancelService;

    private final TokenDecodeService tokenService;
    public CancelController(CancelService cancelService, TokenDecodeService tokenService) {
        this.cancelService = cancelService;
        this.tokenService = tokenService;
    }

    @PostMapping("/{transactionId}")
    public ResponseEntity<?> doctorCancelAppointment(@RequestBody CancelRequest cancelRequest, @PathVariable String transactionId, @RequestHeader("Authorization")String bearerToken) {
        try {
            String token = tokenService.extractToken(bearerToken);
            UserTokenData userTokenData = tokenService.decodeUserToken(token);
            MessageResponse response = cancelService.cancelAppointment(transactionId, userTokenData, bearerToken, cancelRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("An error occurred: " + e.getMessage());
        } catch (WebClientResponseException e) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("An error occurred: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll
    @GetMapping("/getAllCancel")
    public ResponseEntity<?> getAllCancelRequest(){
        try{
            List<Cancel> cancel = cancelService.getAllCancelAppointments();
            return ResponseEntity.ok(cancel);
        } catch (CancelException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //GetAllCancelOfPatient
    @GetMapping("/getAllCancelOfPatient")
    public ResponseEntity<?> getAllCancelOfPatient(@RequestHeader("Authorization")String bearerToken){
        try{
            String token = tokenService.extractToken(bearerToken);
            UserTokenData userTokenData = tokenService.decodeUserToken(token);
            List<Cancel> cancel = cancelService.getAllCancelOfPatients(userTokenData.getSub());
            return ResponseEntity.ok(cancel);
        } catch (CancelException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindByTransactionId
    @GetMapping("/getByTransactionId/{transactionId}")
    public ResponseEntity<?> getByTransactionId(@PathVariable String transactionId){
        try{
            Cancel cancel = cancelService.getCancelByTransactionId(transactionId);
            return ResponseEntity.ok(cancel);
        } catch (CancelException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //GetById
    @GetMapping("/getById/{cancelId}")
    public ResponseEntity<?> getById(@PathVariable Long cancelId){
        try{
            Cancel cancel = cancelService.getCancelById(cancelId);
            return ResponseEntity.ok(cancel);
        } catch (CancelException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Delete Cancel
    @DeleteMapping("/delete/{cancelId}")
    public ResponseEntity<?> deleteCancelById(@PathVariable Long cancelId) {
        try {
            cancelService.deleteCancelById(cancelId);
            return ResponseEntity.ok(new MessageResponse("Delete Successfully!"));
        } catch (CancelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Update Cancel details
    @PutMapping("/update/{cancelId}")
    public ResponseEntity<?> updateApprove(@PathVariable Long cancelId, @RequestBody UpdateCancelDetails updateDetails) {
        try {
            cancelService.updateCancelById(cancelId, updateDetails);
            return ResponseEntity.ok(new MessageResponse("Approve Details updated successfully."));
        } catch (CancelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
