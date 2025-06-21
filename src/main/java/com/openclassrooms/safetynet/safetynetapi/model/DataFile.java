package com.openclassrooms.safetynet.safetynetapi.model;

import lombok.Data;

import java.util.List;

/**
 * Represents the data structure holding all records loaded from the data file.
 *
 * Contains lists of persons, fire stations, and medical records.
 */
@Data
public class DataFile {
    private List<Person> persons;
    private List <FireStation> fireStations;
    private List<MedicalRecord> medicalRecords;
}
