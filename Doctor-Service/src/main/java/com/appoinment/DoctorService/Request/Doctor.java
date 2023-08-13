package com.appoinment.DoctorService.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private boolean enabled;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private Map<String, Object> attributes;

    @NotEmpty(message = "At least one group must be specified")
    private List<String> groups;

    @NotEmpty(message = "At least one credential must be specified")
    private List<CredentialDTO> credentials;
}
