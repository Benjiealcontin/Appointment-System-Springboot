package com.appointment.AppointmentService.Service;

import java.time.LocalDate;

public class DateUtils {

    public static boolean isDateInPast(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        return date.isBefore(currentDate);
    }
}