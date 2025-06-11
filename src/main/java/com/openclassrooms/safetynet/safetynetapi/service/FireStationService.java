package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class FireStationService {

    @Autowired
    private FireStationRepository fireStationRepository;

    public List<FireStation> getAllFireStations() {
        List<FireStation> firestations = fireStationRepository.getFirestations();
        log.info("{} fire station(s) found", firestations.size());
        return firestations;
    }

    public FireStation getFirestationByAddress(String address) {
        log.info("Request received to find firestation by address: '{}'", address);

        FireStation firestation = fireStationRepository.getFirestationByAddress(address);

        if (firestation != null) {
            log.info("Firestation found at address '{}'", address);
        } else {
            log.error("No firestation found at address '{}'", address);
        }

        return firestation;
    }

    public FireStation updateFirestation(FireStation firestation) {
        log.info("Request received to update firestation at address '{}'", firestation.getAddress());

        FireStation firestationToUpdate = fireStationRepository.updateFirestation(firestation);

        if (firestationToUpdate == null) {
            log.error("No firestation found at address '{}', cannot update", firestation.getAddress());
            throw new FireStationNotFoundException("No firestation found at address: " + firestation.getAddress());
        }

        log.info("Firestation at address '{}' updated to station number {}", firestationToUpdate.getAddress(), firestationToUpdate.getStation());
        return firestationToUpdate;
    }

    public void saveFirestation(FireStation firestation) {
        log.info("Request received to save firestation at address '{}' with station number {}",
                firestation.getAddress(), firestation.getStation());

        FireStation existing = fireStationRepository.getFirestationByAddress(firestation.getAddress());

        if (existing != null) {
            log.error("Firestation already exists at address '{}'", firestation.getAddress());
            throw new FireStationAlreadyExistsException("Firestation already exists at address: " + firestation.getAddress());
        }

        fireStationRepository.saveFirestation(firestation);

        log.info("Firestation saved successfully at address '{}' with station number {}",
                firestation.getAddress(), firestation.getStation());
    }

    public void deleteFirestationByAddress(String address) {
        log.info("Request received to delete firestation at address '{}'", address);

        boolean deleted = fireStationRepository.deleteFirstOccurrenceFirestationByAddress(address);

        if (deleted) {
            log.info("Firestation at address '{}' successfully deleted", address);
        } else {
            log.error("No firestation found at address '{}', cannot delete", address);
            throw new FireStationNotFoundException("No firestation found at address: " + address);
        }
    }

    public void deleteFirestationsByStationNumber(int stationNumber) {
        log.info("Request received to delete all firestations with station number {}", stationNumber);

        boolean deleted = fireStationRepository.deleteByStationNumber(stationNumber);

        if (deleted) {
            log.info("All firestations with station number {} successfully deleted", stationNumber);
        } else {
            log.error("No firestations found with station number {}, nothing deleted", stationNumber);
            throw new FireStationNotFoundException("No firestations found with station number: " + stationNumber);
        }
    }


}
