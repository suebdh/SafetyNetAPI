package com.openclassrooms.safetynet.safetynetapi.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a medical record for a person.
 *
 * <p>Contains personal identification fields as well as medical information
 * such as birthdate, medications, and allergies.</p>
 *
 * <p>Uses @Data annotation of Lombok to automatically generate getters, setters,
 * equals, hashCode, and toString methods.</p>
 *
 * @author [Sarar]
 */
@Data
public class MedicalRecord {

    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate birthdate;
    private List<String> medications;
    private List<String> allergies;

}
