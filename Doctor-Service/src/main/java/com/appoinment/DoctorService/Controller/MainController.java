package com.appoinment.DoctorService.Controller;

import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;
import com.appoinment.DoctorService.Service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/doctor")
public class MainController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorsRepository doctorsRepository;

    //Add doctor
    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@Valid @RequestBody DoctorsRequest doctorsRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                // Handle validation errors and return appropriate response
                StringBuilder errorMessage = new StringBuilder();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                }
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }
            // Check if the doctor already exists
            if (doctorService.isDoctorExists(doctorsRequest)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor already exists.");
            }
            return new ResponseEntity<>(doctorService.addDoctor(doctorsRequest), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll doctors
    @GetMapping("/findAll")
    public ResponseEntity<Iterable<Doctors>> getAllDoctors() {
        try{
            Iterable<Doctors> doctors = doctorService.getAllDoctors();
            return new ResponseEntity<>(doctors, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //FindById
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long doctorId) {
        try {
            Doctors doctor = doctorService.getDoctorById(doctorId);
            return ResponseEntity.ok(doctor);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Doctor with ID " + doctorId + " not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //Delete Doctors
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return ResponseEntity.ok(new MessageResponse("Doctor deleted successfully."));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor with ID " + doctorId + " not found.");
        } catch (AddDoctorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Update Doctor
    @PutMapping("/update/{doctorId}")
    public ResponseEntity<MessageResponse> updateDoctor(@PathVariable Long doctorId, @RequestBody DoctorsRequest doctorsRequest) {
        try {
            MessageResponse response = doctorService.updateDoctor(doctorId, doctorsRequest);
            return ResponseEntity.ok(response);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Doctor with ID " + doctorId + " not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }
}
