package com.appointment.KeycloakService.Controller;

import com.appointment.KeycloakService.Exception.KeycloakException;
import com.appointment.KeycloakService.Request.*;
import com.appointment.KeycloakService.Service.KeycloakService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak")
public class KeycloakController {

    private final KeycloakService keycloakService;

    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;

    }

    //Add doctor
    @PostMapping("/AddDoctor")
    public ResponseEntity<String> createDoctor(@RequestBody Doctor doctor, @RequestHeader("Authorization") String bearerToken) {
        try {
                keycloakService.createDoctor(doctor,bearerToken);
                return ResponseEntity.ok("Doctor created successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    //Add Patient
    @PostMapping("/AddPatient")
    public ResponseEntity<String> createUser(@RequestBody Patient patient, @RequestHeader("Authorization") String bearerToken) {
            keycloakService.createPatient(patient,bearerToken);
            return ResponseEntity.ok("Patient created successfully");
    }

    //Get User
    @GetMapping("/getUser/{sub}")
    public ResponseEntity<?> getUser(@PathVariable String sub, @RequestHeader("Authorization") String bearerToken) {
        Patient user = keycloakService.getUser(sub,bearerToken);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Get Doctor
    @GetMapping("/getDoctor/{sub}")
    public ResponseEntity<?> getDoctor(@PathVariable String sub, @RequestHeader("Authorization") String bearerToken) {
        Patient user = keycloakService.getDoctor(sub,bearerToken);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Get All Doctor
    @GetMapping("/getAllDoctor")
    public ResponseEntity<List<GetAllDoctor>> getDoctorsInGroup(@RequestHeader("Authorization") String bearerToken) {
        List<GetAllDoctor> doctors = keycloakService.getDoctorsInGroup(bearerToken);
        if (!doctors.isEmpty()) {
            return ResponseEntity.ok(doctors);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Get All Doctor
    @GetMapping("/getAllPatient")
    public ResponseEntity<List<GetAllPatient>> getPatientsInGroup(@RequestHeader("Authorization") String bearerToken) {
        List<GetAllPatient> patients = keycloakService.getPatientsInGroup(bearerToken);
        if (!patients.isEmpty()) {
            return ResponseEntity.ok(patients);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Delete User
    @DeleteMapping("/deletePatient/{sub}")
    public ResponseEntity<String> deleteUser(@PathVariable String sub, @RequestHeader("Authorization") String bearerToken) {
            keycloakService.deleteUser(sub,bearerToken);
            return ResponseEntity.ok("User deleted successfully");
    }

    //Delete User
    @DeleteMapping("/deleteDoctor/{sub}")
    public ResponseEntity<String> deleteDoctor(@PathVariable String sub, @RequestHeader("Authorization") String bearerToken) {
        keycloakService.deleteDoctor(sub,bearerToken);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    @GetMapping("/home")
    public String home(@RequestHeader("Authorization") String bearerToken) {
        String token = keycloakService.extractToken(bearerToken);
        keycloakService.decodeAndPrintToken(token);
        return "home";
    }

    @PostMapping("/get-token")
    public Mono<String> getToken(@RequestBody FormRequest formRequest) {
        return keycloakService.getToken(formRequest)
                .onErrorResume(KeycloakException.class, ex -> Mono.just(ex.getMessage()));
    }

    //Update Doctor
    @PutMapping("/update-doctor/{userId}")
    public ResponseEntity<String> updateDoctor(@PathVariable String userId, @RequestBody Doctor updatedDoctor,@RequestHeader("Authorization") String bearerToken) {
        keycloakService.updateDoctor(userId, updatedDoctor,bearerToken);
        return ResponseEntity.ok("Doctor updated successfully");
    }

    //Update Patient
    @PutMapping("/update-patient/{userId}")
    public ResponseEntity<String> updatePatient(@PathVariable String userId, @RequestBody Patient updatedPatient,@RequestHeader("Authorization") String bearerToken) {
        keycloakService.updatePatient(userId, updatedPatient,bearerToken);
        return ResponseEntity.ok("Patient updated successfully");
    }

}
