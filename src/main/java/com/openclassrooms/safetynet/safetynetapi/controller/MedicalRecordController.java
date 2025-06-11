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

@Log4j2
@RestController
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/medicalrecord")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        log.info("GET request received for all medical records");

        List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();

        log.info("Returning {} medical record(s)", medicalRecords.size());

        return ResponseEntity.ok(medicalRecords);
    }

    @PostMapping("/medicalrecord")
    public ResponseEntity<?> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord saved = medicalRecordService.saveMedicalRecord(medicalRecord);
            log.info("Medical record for {} {} added successfully.", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (MedicalRecordAlreadyExistsException ex) {
            log.warn("Cannot add medical record for {} {}: already exists.", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Medical record already exists for this person.");
        }
    }

    @PutMapping("/medicalrecord")
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord updated = medicalRecordService.updateMedicalRecord(medicalRecord);
            log.info("Medical record for {} {} successfully updated.",
                    updated.getFirstName(), updated.getLastName());
            return ResponseEntity.ok(updated);
        } catch (MedicalRecordNotFoundException ex) {
            log.warn("Cannot update medical record for {} {}: not found.",
                    medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medical record not found for this person.");
        }
    }

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
