package com.openclassrooms.safetynet.safetynetapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing the medical information of a person.
 *
 * <p>This class is used to transfer medical record data between layers,
 * especially in API requests and responses. It includes personal identification
 * fields and medical details such as medications and allergies.</p>
 *
 * <p>The birthdate is serialized/deserialized in the format "MM/dd/yyyy".</p>
 *
 * @author Sarar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate birthdate;
    private List<String> medications;
    private List<String> allergies;
}
