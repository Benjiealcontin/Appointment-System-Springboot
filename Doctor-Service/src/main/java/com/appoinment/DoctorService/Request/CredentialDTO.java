package com.appoinment.DoctorService.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDTO {
    private String type;

    @NotBlank(message = "Password is required")
    private String value;

    private boolean temporary;
}
