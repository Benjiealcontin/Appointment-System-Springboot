package com.appointment.AvailableService.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRequest {
    @NotBlank(message = "Doctor name must not be blank")
    private String doctorName;
    @NotBlank(message = "Doctor working hours must not be blank")
    private String workingHours;
}
