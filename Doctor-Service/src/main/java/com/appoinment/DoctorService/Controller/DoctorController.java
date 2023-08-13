package com.appoinment.DoctorService.Controller;



import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Request.Doctor;
import com.appoinment.DoctorService.Request.GetAllDoctor;
import com.appoinment.DoctorService.Service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/addDoctor")
    public ResponseEntity<?> createDoctor(
            @RequestBody Doctor doctorRequest,
            @RequestHeader("Authorization") String bearerToken
    ) {
        try {
            doctorService.createDoctor(doctorRequest, bearerToken);
            return ResponseEntity.ok("Doctor created successfully.");
        } catch (AddDoctorException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }

    //Find Doctor by I'd
    @GetMapping("/getDoctorById/{sub}")
    public ResponseEntity<?> getDoctor(@PathVariable String sub, @RequestHeader("Authorization") String bearerToken) {
        try {
            Doctor user = doctorService.getDoctor(sub,bearerToken);
            return ResponseEntity.ok(user);
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //Get All Doctors
    @GetMapping("/getAllDoctor")
    public ResponseEntity<?> getDoctorsInGroup(@RequestHeader("Authorization") String bearerToken) {

        try {
            List<GetAllDoctor> doctors = doctorService.getDoctorsInGroup(bearerToken);
            return ResponseEntity.ok(doctors);
        } catch (DoctorsNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    //Delete Doctor
    @DeleteMapping("/deleteDoctor/{userId}")
    public ResponseEntity<String> deleteDoctor(@PathVariable String userId, @RequestHeader("Authorization") String bearerToken) {
        try{
            doctorService.deleteDoctor(userId,bearerToken);
            return ResponseEntity.ok("Doctor deleted successfully");
        }catch (DoctorsNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    //Update Doctor Info
    @PutMapping("/update-doctor/{userId}")
    public ResponseEntity<String> updateDoctor(@PathVariable String userId, @RequestBody Doctor updatedDoctor,@RequestHeader("Authorization") String bearerToken) {
        try{
            doctorService.updateDoctor(userId, updatedDoctor,bearerToken);
            return ResponseEntity.ok("Doctor updated successfully");
        }catch (DoctorsNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
