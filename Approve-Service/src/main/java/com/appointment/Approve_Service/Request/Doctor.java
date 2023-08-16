package com.appointment.Approve_Service.Request;

import com.appoinment.DoctorService.Request.CredentialDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private String username;
    private String email;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private Map<String, Object> attributes;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
