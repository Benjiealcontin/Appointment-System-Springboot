package com.appoinment.DoctorService.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Doctors")
public class Doctors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String doctorName;
    private String location;
    @ElementCollection
    private List<String> specializations;
    @ElementCollection
    private List<String> workingHours;
    @ElementCollection
    private List<String> qualifications;
    @ElementCollection
    private List<String> contactInformation;
    @ElementCollection
    private List<String> professionalExperience;
}
