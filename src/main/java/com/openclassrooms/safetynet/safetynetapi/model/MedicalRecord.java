package com.openclassrooms.safetynet.safetynetapi.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a medical record for a person.
 *
 * <p>Contains personal identification fields as well as medical information
 * such as birthdate, medications, and allergies.</p>
 *
 * <p>The birthdate is formatted using MM/dd/yyyy via Jackson's @JsonFormat annotation.</p>
 * <p>Uses @Data annotation of Lombok to automatically generate getters, setters,
 * equals, hashCode, and toString methods.</p>
 *
 * @author [Sarar]
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MedicalRecord {

    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate birthdate;
    private List<String> medications;
    private List<String> allergies;

}
