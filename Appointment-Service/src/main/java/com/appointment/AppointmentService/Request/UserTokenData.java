package com.appointment.AppointmentService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenData {
    private String givenName;
    private String familyName;
    private String phoneNumber;
    private String email;
    private int age;
    private String sub;
    private String gender;
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;

    public String getFullName() {
        return givenName + " " + familyName;
    }

    public String getAddress() {
        return streetAddress + ", " + locality + ", " + region + ", " + postalCode + ", " + country;
    }

}
