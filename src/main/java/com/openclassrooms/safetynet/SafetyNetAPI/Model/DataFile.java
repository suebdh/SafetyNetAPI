package com.openclassrooms.safetynet.SafetyNetAPI.Model;

import lombok.Data;

import java.util.List;

@Data
public class DataFile {
    private List<Person> persons;
    private List <FireStation> firestations;
    private List<MedicalRecord> medicalrecords;
}
