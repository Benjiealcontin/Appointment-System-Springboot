package com.appointment.CancelService.Request;

import lombok.*;


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
