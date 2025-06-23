package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class InMemoryMedicalRecordRepository implements MedicalRecordRepository {
    @Autowired
    private DataLoader dataLoader;

    private List<MedicalRecord> medicalRecords;

    /**
     * Initializes the in-memory mutable list of medical records.
     * This method runs after dependency injection, loading medical records from the JSON data file via DataLoader.
     * It copies them into a new ArrayList to allow modifications during runtime.
     * Logs the count of medical records loaded at debug level.
     */
    @PostConstruct
    public void init() {
        this.medicalRecords = new ArrayList<>(dataLoader.getDataFile().getMedicalRecords());
        log.debug("Medical records loaded: {}", medicalRecords.size());
    }

    /**
     * Updates the DataFile with the current list of medical records and saves changes to the JSON file.
     */
    private void persistChanges() {
        // Update the source DataFile
        dataLoader.getDataFile().setMedicalRecords(medicalRecords);
        // Persist changes to the JSON file
        dataLoader.saveJsonFile();
    }

    /**
     * Retrieves all medical records stored in memory.
     *
     * @return a list of all MedicalRecord objects; never null but can be empty.
     *         The returned list is a copy and modifications on it won't affect the internal list.
     */
    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        log.debug("Fetching all medical records. Total: {}", medicalRecords.size());
        return new ArrayList<>(medicalRecords);
    }

    /**
     * Retrieves the medical record matching the given first name and last name.
     *
     * <p>This method performs a case-insensitive search on both first name and last name
     * and returns the first matching record found in the in-memory list.</p>
     *
     * @param firstName the first name to search for (case-insensitive)
     * @param lastName the last name to search for (case-insensitive)
     * @return the first matching MedicalRecord if found; otherwise, returns null
     */
    @Override
    public MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        String trimmedFirstName = firstName.trim().replaceAll("\\s+", " ");
        String trimmedLastName = lastName.trim().replaceAll("\\s+", " ");
        List<MedicalRecord> results = medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equalsIgnoreCase(trimmedFirstName)
                        && mr.getLastName().equalsIgnoreCase(trimmedLastName))
                .toList();

        if (!results.isEmpty()) {
            log.debug("Medical record found for {} {}", firstName, lastName);
            return results.getFirst(); // We return the first one found
        } else {
            log.debug("No medical record found for {} {}", firstName, lastName);
            return null;
        }
    }

    /**
     * Adds a new medical record to the in-memory list, updates the main data file, and persists the changes to the external JSON file.
     *
     * @param medicalRecord the medical record to be saved
     */
    @Override
    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {

        medicalRecords.add(medicalRecord);
        log.debug("Medical record for {} {} saved with birthdate {}, medications {}, and allergies {}",
                medicalRecord.getFirstName(),
                medicalRecord.getLastName(),
                medicalRecord.getBirthdate(),
                medicalRecord.getMedications(),
                medicalRecord.getAllergies());

        persistChanges();

        return medicalRecord;
    }

    /**
     * Updates an existing medical record matching the first name and last name.
     * <p>
     * If a matching record is found, updates its birthdate, medications, and allergies,
     * then persists the changes to the JSON data file.
     * </p>
     *
     * @param medicalRecord the medical record containing updated information
     * @return the updated MedicalRecord if found and updated; otherwise, returns null
     */
    @Override
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        for (MedicalRecord mr : medicalRecords) {
            if (mr.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                    && mr.getLastName().equalsIgnoreCase(medicalRecord.getLastName())) {

                mr.setBirthdate(medicalRecord.getBirthdate());
                mr.setMedications(medicalRecord.getMedications());
                mr.setAllergies(medicalRecord.getAllergies());

                log.debug("Medical record for {} {} updated: birthdate={}, medications={}, allergies={}",
                        mr.getFirstName(),
                        mr.getLastName(),
                        mr.getBirthdate(),
                        mr.getMedications(),
                        mr.getAllergies());

                persistChanges();

                return mr;
            }
        }

        log.debug("No medical record found for {} {}, update skipped",
                medicalRecord.getFirstName(),
                medicalRecord.getLastName());

        return null;
    }

    /**
     * Deletes a medical record identified by first name and last name.
     * If the record is found and deleted, the in-memory list and the JSON file are updated accordingly.
     *
     * @param firstName the first name of the medical record to delete
     * @param lastName the last name of the medical record to delete
     * @return true if the record was found and deleted; false otherwise
     */
    @Override
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        boolean removed = medicalRecords.removeIf(mr ->
                mr.getFirstName().equalsIgnoreCase(firstName) &&
                        mr.getLastName().equalsIgnoreCase(lastName));

        if (removed) {
            log.debug("Medical record for {} {} deleted", firstName, lastName);

            persistChanges();
        } else {
            log.debug("No medical record found for {} {}, nothing deleted", firstName, lastName);
        }

        return removed;
    }
}
