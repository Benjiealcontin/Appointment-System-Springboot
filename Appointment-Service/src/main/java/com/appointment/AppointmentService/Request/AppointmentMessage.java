package com.appointment.AppointmentService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentMessage {
    private UserTokenData userTokenData;
    private AppointmentData appointmentData;

}
