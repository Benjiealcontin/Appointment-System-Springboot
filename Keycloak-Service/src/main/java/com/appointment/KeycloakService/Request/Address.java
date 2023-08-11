package com.appointment.KeycloakService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;
}
