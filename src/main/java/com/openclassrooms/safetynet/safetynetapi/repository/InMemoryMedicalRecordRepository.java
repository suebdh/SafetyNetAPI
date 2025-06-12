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

    @PostConstruct
    public void init() {
        this.medicalRecords = new ArrayList<>(dataLoader.getDataFile().getMedicalrecords());
        log.debug("Medical records loaded: {}", medicalRecords.size());
    }

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        log.debug("Fetching all medical records. Total: {}", medicalRecords.size());
        return medicalRecords;
    }

    @Override
    public MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        List<MedicalRecord> results = medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equalsIgnoreCase(firstName)
                        && mr.getLastName().equalsIgnoreCase(lastName))
                .toList();

        if (!results.isEmpty()) {
            log.debug("Medical record found for {} {}", firstName, lastName);
            return results.get(0); // On retourne le premier trouvé
        } else {
            log.debug("No medical record found for {} {}", firstName, lastName);
            return null;
        }
    }

    @Override
    public void saveMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecords.add(medicalRecord);
        log.debug("Medical record for {} {} saved with birthdate {}, medications {}, and allergies {}",
                medicalRecord.getFirstName(),
                medicalRecord.getLastName(),
                medicalRecord.getBirthdate(),
                medicalRecord.getMedications(),
                medicalRecord.getAllergies());
    }

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

                return mr;
            }
        }

        log.debug("No medical record found for {} {}, update skipped",
                medicalRecord.getFirstName(),
                medicalRecord.getLastName());

        return null;
    }

    @Override
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        boolean removed = medicalRecords.removeIf(mr ->
                mr.getFirstName().equalsIgnoreCase(firstName) &&
                        mr.getLastName().equalsIgnoreCase(lastName));

        if (removed) {
            log.debug("Medical record for {} {} deleted", firstName, lastName);
        } else {
            log.debug("No medical record found for {} {}, nothing deleted", firstName, lastName);
        }

        return removed;
    }
}
