package com.appoinment.DoctorService.Request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorsRequest {
    @NotBlank(message = "Doctor name must not be blank")
    private String doctorName;

    @NotEmpty(message = "Specializations list must not be empty")
    private List<String> specializations;

    @NotEmpty(message = "Working Hours must not be empty")
    private List<String> workingHours;

    @NotEmpty(message = "Qualifications list must not be empty")
    private List<String> qualifications;

    @NotEmpty(message = "Contact information list must not be empty")
    private List<String> contactInformation;

    @NotEmpty(message = "Professional experience list must not be empty")
    private List<String> professionalExperience;
}
