package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MedicalRecordRepository {
    List<MedicalRecord> getAllMedicalRecords();

    MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName);

    MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord);

    MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord);

    boolean deleteMedicalRecord(String firstName, String lastName);
}
