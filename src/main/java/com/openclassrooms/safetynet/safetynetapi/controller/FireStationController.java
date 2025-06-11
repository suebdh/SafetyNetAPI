package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.service.FireStationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<List<FireStation>> getFirestations() {
        log.info("GET request received for all firestations");

        List<FireStation> fireStations = fireStationService.getAllFireStations();

        log.info("Returning {} firestation(s)", fireStations.size());

        return ResponseEntity.ok(fireStations);
    }

    @PostMapping("/firestation")
    public ResponseEntity<?> addFireStation(@RequestBody FireStation fireStation) {
        try {
            FireStation saved = fireStationService.saveFirestation(fireStation);
            log.info("Firestation at address '{}' with station number {} added successfully.",
                    fireStation.getAddress(), fireStation.getStation());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (FireStationAlreadyExistsException ex) {
            log.warn("Cannot add firestation at address '{}': already exists.", fireStation.getAddress());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Firestation already exists at this address.");
        }
    }

    @PutMapping("/firestation")
    public ResponseEntity<?> updateFireStation(@RequestBody FireStation fireStation) {
        try {
            FireStation updated = fireStationService.updateFirestation(fireStation);
            log.info("Firestation at address '{}' successfully updated to station number {}.",
                    updated.getAddress(), updated.getStation());
            return ResponseEntity.ok(updated);
        } catch (FireStationNotFoundException ex) {
            log.warn("Cannot update firestation at address '{}': not found.", fireStation.getAddress());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Firestation not found at this address.");
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestParam String address) {
        try {
            fireStationService.deleteFirestationByAddress(address);
            log.info("Firestation at address '{}' deleted successfully.", address);
            return ResponseEntity.ok("Firestation at address '" + address + "' deleted successfully.");
        } catch (FireStationNotFoundException ex) {
            log.warn("Cannot delete firestation at address '{}': not found.", address);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Firestation not found at address: " + address);
        }
    }

    @DeleteMapping("/firestations")
    public ResponseEntity<String> deleteFireStationsByStationNumber(@RequestParam int stationNumber) {
        try {
            fireStationService.deleteFirestationsByStationNumber(stationNumber);
            log.info("All firestations with station number {} deleted successfully.", stationNumber);
            return ResponseEntity.ok("All firestations with station number " + stationNumber + " deleted successfully.");
        } catch (FireStationNotFoundException ex) {
            log.warn("Cannot delete firestations with station number {}: not found.", stationNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No firestations found with station number: " + stationNumber);
        }
    }

}
