package com.appoinment.DoctorService.Controller;


import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;
import com.appoinment.DoctorService.Service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    //Add doctor
    @PostMapping("/add")
    private ResponseEntity<?> addDoctor(@Valid @RequestBody DoctorsRequest doctorsRequest, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        try {
            boolean doctorExists = doctorService.checkIfDoctorExists(doctorsRequest.getDoctorName());
            if (doctorExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor with the given name already exists.");
            }

            MessageResponse response = doctorService.addDoctor(doctorsRequest);
            return ResponseEntity.ok(response);
        } catch (AddDoctorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll doctors
    @GetMapping("/findAll")
    private ResponseEntity<?> getAllDoctors() {
        try {
            List<Doctors> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok(doctors);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindById
    @GetMapping("/getDoctorById/{doctorId}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long doctorId) {
        try {
            Doctors doctor = doctorService.getDoctorById(doctorId);
            return ResponseEntity.ok(doctor);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    //Find By Doctor Name
    @GetMapping("/findByName/{doctorName}")
    public ResponseEntity<?> getDoctorByName(@PathVariable String doctorName) {
        try {
            Doctors doctor = doctorService.getDoctorByName(doctorName);
            return ResponseEntity.ok(doctor);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Find by Doctor Specializations
    @GetMapping("/findBySpecialization/{specialization}")
    private ResponseEntity<?> getDoctorBySpecialization(@PathVariable String specialization) {
        try {
            Doctors doctors = doctorService.getDoctorsBySpecialization(specialization);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    //Delete Doctors
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return ResponseEntity.ok(new MessageResponse("Delete Successfully!"));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (AddDoctorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }


    //Update Doctor
    @PutMapping("/update/{doctorId}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long doctorId, @RequestBody DoctorsRequest doctorsRequest) {
        try {
            doctorService.updateDoctor(doctorId, doctorsRequest);
            return ResponseEntity.ok(new MessageResponse("Doctor updated successfully."));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }
}
