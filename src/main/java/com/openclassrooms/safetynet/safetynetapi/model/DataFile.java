package com.openclassrooms.safetynet.safetynetapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents the data structure holding all records loaded from the data file.
 * Contains lists of persons, fire stations, and medical records.
 */
@Getter
@Setter
public class DataFile {
    private List<Person> persons;
    @JsonProperty("firestations")
    private List<FireStation> fireStations;
    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords;
}
