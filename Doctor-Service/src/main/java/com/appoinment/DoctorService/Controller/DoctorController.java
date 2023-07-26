package com.appoinment.DoctorService.Controller;


import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;
import com.appoinment.DoctorService.Service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    private final DoctorsRepository doctorsRepository;
    public DoctorController(DoctorService doctorService, DoctorsRepository doctorsRepository) {
        this.doctorService = doctorService;
        this.doctorsRepository = doctorsRepository;
    }

    //Add doctor
    @PostMapping("/add")
    private ResponseEntity<?> addDoctor(@Valid @RequestBody DoctorsRequest doctorsRequest, BindingResult result) {
        try {
            //Form Validation
            if (result.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                }
                return ResponseEntity.badRequest().body(errorMessage.toString());
            }

            //Check if Doctor is Exist
            boolean doctors = doctorsRepository.existsByDoctorName(doctorsRequest.getDoctorName());
            if (doctors) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor with the given name already exists.");
            }

            return new ResponseEntity<>(doctorService.addDoctor(doctorsRequest), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindAll doctors
    @GetMapping("/findAll")
    private ResponseEntity<?> getAllDoctors() {
        try{
            return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
        }catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //FindById
    @GetMapping("/{doctorId}")
    private ResponseEntity<?> getDoctorById(@PathVariable Long doctorId) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //Find By Doctor Name
    @GetMapping("/findByName/{doctorName}")
    public ResponseEntity<?> getDoctorByName(@PathVariable String doctorName) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorByName(doctorName));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //Find by Doctor Specializations
    @GetMapping("/findBySpecialization/{specializations}")
    private ResponseEntity<?> getDoctorBySpecialization(@PathVariable String specializations) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorBySpecialization(specializations));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }

    //Delete Doctors
    @DeleteMapping("/delete/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            return ResponseEntity.ok(doctorService.deleteDoctor(doctorId));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AddDoctorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Update Doctor
    @PutMapping("/update/{doctorId}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long doctorId, @RequestBody DoctorsRequest doctorsRequest) {
        try {
            return ResponseEntity.ok(doctorService.updateDoctor(doctorId, doctorsRequest));
        } catch (DoctorsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("An error occurred: " + e.getMessage()));
        }
    }
}
