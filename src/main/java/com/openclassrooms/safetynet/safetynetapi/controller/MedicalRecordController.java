package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.service.MedicalRecordService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing medical records in the system.
 *
 * <p>This controller handles HTTP requests for CRUD operations on medical records.
 * It delegates business logic to the MedicalRecordService and relies on
 * global exception handling for expected error cases such as record not found or already exists.</p>
 *
 * <p>Endpoints supported:
 * <ul>
 *   <li>GET /medicalrecord : Retrieve all medical records</li>
 *   <li>POST /medicalrecord : Add a new medical record</li>
 *   <li>PUT /medicalrecord : Update an existing medical record</li>
 *   <li>DELETE /medicalrecord : Delete a medical record by first and last name</li>
 * </ul>
 * </p>
 *
 * <p>All exceptions like MedicalRecordNotFoundException and MedicalRecordAlreadyExistsException} are handled globally via {@code GlobalExceptionHandler}.</p>
 */
@Log4j2
@RestController
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * Retrieves all medical records from the system.
     *
     * @return ResponseEntity containing the list of all medical records with HTTP status 200 OK.
     */
    @GetMapping("/medicalrecord")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        log.info("GET request received for all medical records");

        List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();

        log.info("Returning {} medical record(s)", medicalRecords.size());

        return ResponseEntity.ok(medicalRecords);
    }

    /**
     * Adds a new medical record to the system.
     *
     * <p>This method delegates the creation of the medical record to the MedicalRecordService.
     * If the medical record already exists for the given person, a MedicalRecordAlreadyExistsException will be thrown and handled globally.</p>
     *
     * @param medicalRecord the MedicalRecord object to be added, provided in the request body
     * @return a ResponseEntity containing the saved MedicalRecord and HTTP status 201 (Created)
     * @throws MedicalRecordAlreadyExistsException if a record already exists for the person
     */
    @PostMapping("/medicalrecord")
    public ResponseEntity<?> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {

        MedicalRecord saved = medicalRecordService.saveMedicalRecord(medicalRecord);
        log.info("Medical record for {} {} added successfully.", medicalRecord.getFirstName(), medicalRecord.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing medical record in the system.
     *
     * <p>This method delegates the update operation to the MedicalRecordService
     * If the medical record for the specified person does not exist, a MedicalRecordNotFoundException will be thrown and handled globally.</p>
     *
     * @param medicalRecord the edicalRecord object containing updated information, provided in the request body
     * @return a ResponseEntity containing the updated MedicalRecord and HTTP status 200 (OK)
     * @throws MedicalRecordNotFoundException if the record to update is not found
     */
    @PutMapping("/medicalrecord")
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {

        MedicalRecord updated = medicalRecordService.updateMedicalRecord(medicalRecord);
        log.info("Medical record for {} {} successfully updated.",
                updated.getFirstName(), updated.getLastName());
        return ResponseEntity.ok(updated);

    }

    /**
     * Deletes a medical record identified by first name and last name.
     *
     * <p>This method calls the MedicalRecordService to delete the record.
     * If the medical record does not exist, a MedicalRecordNotFoundException is thrown and handled globally.</p>
     *
     * @param firstName the first name of the person whose medical record is to be deleted
     * @param lastName  the last name of the person whose medical record is to be deleted
     * @return a ResponseEntity  with a confirmation message and HTTP status 200 (OK)
     * @throws MedicalRecordNotFoundException if the record to delete is not found
     */
    @DeleteMapping("/medicalrecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);
            log.info("Medical record for {} {} deleted successfully.", firstName, lastName);
            return ResponseEntity.ok("Medical record for " + firstName + " " + lastName + " deleted successfully.");
        } catch (MedicalRecordNotFoundException ex) {
            log.warn("Cannot delete medical record for {} {}: not found.", firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Medical record not found for " + firstName + " " + lastName);
        }
    }
}
