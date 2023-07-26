package com.appointment.Approve_Service.Controller;

import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.Approve_Service.Exception.ApproveNotFoundException;
import com.appointment.Approve_Service.Exception.WebClientException;
import com.appointment.Approve_Service.Request.ApproveRequest;
import com.appointment.Approve_Service.Service.ApproveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/approve")
public class ApproveController {


    private final ApproveService approveService;

    public ApproveController(ApproveService approveService) {
        this.approveService = approveService;
    }


    //Approve Appointment
    @PostMapping("/{transactionId}")
    private ResponseEntity<?> approveAppointment(@PathVariable String transactionId,@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(approveService.approveAppointment(transactionId,token));
        } catch (AppointmentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (WebClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll Approve Appointment
    @GetMapping("/getAllApprove")
    private ResponseEntity<?> getAllApprove(){
        try{
            return ResponseEntity.ok(approveService.getAllApproveAppointment());
        }catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindById
    @GetMapping("/findById/{approveId}")
    private ResponseEntity<?> getApproveById(@PathVariable Long approveId){
        try{
            return ResponseEntity.ok(approveService.getApproveById(approveId));
        }catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Delete by Id
    @DeleteMapping("/delete/{approveId}")
    private ResponseEntity<?> deleteApproveById(@PathVariable Long approveId){
        try{
            return ResponseEntity.ok(approveService.deleteApproveById(approveId));
        }catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/update/{approveId}")
    private ResponseEntity<?> updateApprove(@PathVariable Long approveId, @RequestBody ApproveRequest approveRequest){
        try{
            return ResponseEntity.ok(approveService.updateApproveById(approveId,approveRequest));
        }catch (ApproveNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
