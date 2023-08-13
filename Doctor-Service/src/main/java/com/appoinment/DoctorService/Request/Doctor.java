package com.appoinment.DoctorService.Request;

import lombok.*;
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
    private List<String> groups;
    private List<CredentialDTO> credentials;
}
