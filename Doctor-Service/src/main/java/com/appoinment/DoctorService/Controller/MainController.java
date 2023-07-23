package com.appoinment.DoctorService.Controller;

import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
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
    public ResponseEntity<?> createStudent(@Valid @RequestBody DoctorsRequest doctorsRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                // Handle validation errors and return appropriate response
                StringBuilder errorMessage = new StringBuilder();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                }
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }
            return new ResponseEntity<>(doctorService.addDoctor(doctorsRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
}
