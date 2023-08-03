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
    private String sub;
    private String gender;
    private int age;

    public String getFullName() {
        return givenName + " " + familyName;
    }

}
