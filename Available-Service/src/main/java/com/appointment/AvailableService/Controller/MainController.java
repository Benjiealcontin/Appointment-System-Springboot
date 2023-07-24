package com.appointment.AvailableService.Controller;

import com.appointment.AvailableService.Request.AvailableRequest;
import com.appointment.AvailableService.Service.AvailableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/available")
public class MainController {

    @Autowired
    private AvailableService availableService;

    //Add Schedule of doctors
    @PostMapping("/add")
    public ResponseEntity<?> addWorkingHours(@Valid @RequestBody AvailableRequest availableRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                // Handle validation errors and return appropriate response
                StringBuilder errorMessage = new StringBuilder();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                }
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }
            return new ResponseEntity<>(availableService.addWorkingHours(availableRequest), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
