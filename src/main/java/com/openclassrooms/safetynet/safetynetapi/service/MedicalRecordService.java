package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.getAllMedicalRecords();
        log.info("Retrieved {} medical records", records.size());
        return records;
    }

    public MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        log.info("Request received to find medical record for: {} {}", firstName, lastName);

        MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(firstName, lastName);

        if (record != null) {
            log.info("Medical record found for {} {}", firstName, lastName);
        } else {
            log.error("No medical record found for {} {}", firstName, lastName);
        }

        return record;
    }

    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
        log.info("Request received to save medical record for {} {}",
                medicalRecord.getFirstName(), medicalRecord.getLastName());

        MedicalRecord existing = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                medicalRecord.getFirstName(), medicalRecord.getLastName());

        if (existing != null) {
            log.error("Medical record already exists for {} {}",
                    medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new MedicalRecordAlreadyExistsException(
                    "Medical record already exists for " +
                            medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
        }

        medicalRecordRepository.saveMedicalRecord(medicalRecord);

        log.info("Medical record successfully saved for {} {} with birthdate {}, medications {}, and allergies {}",
                medicalRecord.getFirstName(),
                medicalRecord.getLastName(),
                medicalRecord.getBirthdate(),
                medicalRecord.getMedications(),
                medicalRecord.getAllergies());

        return medicalRecord;
    }

    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        log.info("Request received to update medical record for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());

        MedicalRecord updated = medicalRecordRepository.updateMedicalRecord(medicalRecord);

        if (updated == null) {
            log.error("No medical record found for {} {}, cannot update", medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new MedicalRecordNotFoundException("No medical record found for: " +
                    medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
        }

        log.info("Medical record for {} {} updated successfully", updated.getFirstName(), updated.getLastName());
        return updated;
    }

    public void deleteMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        log.info("Request received to delete medical record for {} {}", firstName, lastName);

        boolean deleted = medicalRecordRepository.deleteMedicalRecord(firstName, lastName);

        if (deleted) {
            log.info("Medical record for {} {} successfully deleted", firstName, lastName);
        } else {
            log.error("No medical record found for {} {}, cannot delete", firstName, lastName);
            throw new MedicalRecordNotFoundException("No medical record found for: " + firstName + " " + lastName);
        }
    }

}

