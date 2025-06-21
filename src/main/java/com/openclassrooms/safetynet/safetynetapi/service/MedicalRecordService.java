package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.service.mapper.MedicalRecordMapper;
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

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    /**
     * Retrieves all medical records from the repository.
     *
     * @return a list of all MedicalRecord objects currently stored
     */
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.getAllMedicalRecords();
        log.info("Retrieved {} medical records", records.size());
        return records.stream()
                .map(medicalRecordMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a medical record by the person's first name and last name.
     *
     * @param firstName the first name of the person whose medical record is requested
     * @param lastName  the last name of the person whose medical record is requested
     * @return the MedicalRecordDTO object if found, or null if no matching record exists
     */
    public MedicalRecordDTO getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        log.info("Request received to find medical record for: {} {}", firstName, lastName);

        MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(firstName, lastName);

        if (record != null) {
            log.info("Medical record found for {} {}", firstName, lastName);
            return medicalRecordMapper.toDTO(record);
        } else {
            log.error("No medical record found for {} {}", firstName, lastName);
            throw new MedicalRecordNotFoundException("No medical record found for: " + firstName + " " + lastName);
        }

    }
    
    /**
     * Saves a new medical record in the system.
     *
     * @param medicalRecordDTO the MedicalRecordDTO to be saved
     * @return the saved MedicalRecordDTO
     * @throws MedicalRecordAlreadyExistsException if a medical record already exists for the given person
     */
    public MedicalRecordDTO saveMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        log.info("Request received to save medical record for {} {}",
                medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());

        MedicalRecord existing = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());

        if (existing != null) {
            log.error("Medical record already exists for {} {}",
                    medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());
            throw new MedicalRecordAlreadyExistsException(
                    "Medical record already exists for " +
                            medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName());
        }

        MedicalRecord medicalRecordEntity = medicalRecordMapper.toEntity(medicalRecordDTO);
        MedicalRecord updated = medicalRecordRepository.saveMedicalRecord(medicalRecordEntity);

        log.info("Medical record successfully saved for {} {} with birthdate {}, medications {}, and allergies {}",
                medicalRecordDTO.getFirstName(),
                medicalRecordDTO.getLastName(),
                medicalRecordDTO.getBirthdate(),
                medicalRecordDTO.getMedications(),
                medicalRecordDTO.getAllergies());

        return medicalRecordMapper.toDTO(updated);
    }

    /**
     * Updates an existing medical record in the system.
     *
     * @param medicalRecordDTO the MedicalRecordDTO containing updated data
     * @return the updated medicalRecordDTO object
     * @throws MedicalRecordNotFoundException if the medical record to update does not exist
     */
    public MedicalRecordDTO  updateMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        log.info("Request received to update medical record for {} {}", medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());

        MedicalRecord medicalRecordEntity = medicalRecordMapper.toEntity(medicalRecordDTO);
        MedicalRecord updated = medicalRecordRepository.updateMedicalRecord(medicalRecordEntity);

        if (updated == null) {
            log.error("No medical record found for {} {}, cannot update", medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName());
            throw new MedicalRecordNotFoundException("No medical record found for: " +
                    medicalRecordDTO.getFirstName() + " " + medicalRecordDTO.getLastName());
        }

        log.info("Medical record for {} {} updated successfully", updated.getFirstName(), updated.getLastName());
        return medicalRecordMapper.toDTO(updated);
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

