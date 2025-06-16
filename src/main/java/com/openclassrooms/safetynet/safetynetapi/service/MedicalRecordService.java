package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing medical records.
 *
 * <p>This class handles the business logic related to MedicalRecord entities, including retrieval, creation, update, and deletion.
 * It delegates data persistence to the MedicalRecordRepository and throws custom exceptions when necessary.</p>
 *
 * <p>All methods log key actions and errors to facilitate debugging and monitoring.</p>
 */
@Log4j2
@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Retrieves all medical records from the repository.
     *
     * @return a list of all MedicalRecord objects currently stored
     */
    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.getAllMedicalRecords();
        log.info("Retrieved {} medical records", records.size());
        return records;
    }

    /**
     * Retrieves a medical record by the person's first name and last name.
     *
     * @param firstName the first name of the person whose medical record is requested
     * @param lastName  the last name of the person whose medical record is requested
     * @return the MedicalRecord object if found, or null if no matching record exists
     */
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

    /**
     * Saves a new medical record in the system.
     *
     * <p>Before saving, checks if a medical record already exists for the given first and last name.
     * If a record exists, throws a MedicalRecordAlreadyExistsException.</p>
     *
     * @param medicalRecord the MedicalRecord object to be saved
     * @return the saved MedicalRecord object
     * @throws MedicalRecordAlreadyExistsException if a medical record already exists for the given person
     */
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

    /**
     * Updates an existing medical record in the system.
     *
     * <p>Attempts to update the medical record with the given information.
     * If no matching medical record is found, a MedicalRecordNotFoundException is thrown.</p>
     *
     * @param medicalRecord the MedicalRecord object containing updated data
     * @return the updated MedicalRecord object
     * @throws MedicalRecordNotFoundException if the medical record to update does not exist
     */
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

    /**
     * Deletes a medical record identified by the given first and last name.
     *
     * <p>This method attempts to remove the medical record corresponding to the specified first and last name from the repository.
     * If no such record exists, it throws a MedicalRecordNotFoundException.</p>
     *
     * @param firstName the first name of the person whose medical record should be deleted
     * @param lastName the last name of the person whose medical record should be deleted
     * @throws MedicalRecordNotFoundException if no medical record is found for the given names
     */
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

