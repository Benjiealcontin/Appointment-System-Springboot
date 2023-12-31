package com.appointment.KeycloakService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDTO {
    private String type;
    private String value;
    private boolean temporary;
}
