package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.FireStationDTO;
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

    /**
     * Handles GET requests to retrieve all fire stations.
     *
     * @return ResponseEntity containing:
     * - HTTP 200 OK and the list of fire stations if any exist,
     * - HTTP 204 No Content if no fire stations are found.
     */
    @GetMapping("/firestations")
    public ResponseEntity<List<FireStationDTO>> getFireStations() {
        log.info("GET request received for all firestations");

        List<FireStationDTO> fireStationsDTOs = fireStationService.getAllFireStations();

        log.info("Returning {} firestation(s)", fireStationsDTOs.size());
        return ResponseEntity.ok(fireStationsDTOs);
    }

    /**
     * Handles HTTP POST requests to add a new fire station.
     * <p>
     * Receives a FireStationDTO object in the request body and delegates the creation to the service layer.
     * Returns the created fire station data if the operation is successful.
     * </p>
     *
     * @param fireStationDTO the FireStationDTO object containing the fire station details to be added
     * @return ResponseEntity with HTTP 201 (Created) and the saved FireStationDTO
     */
    @PostMapping("/firestation")
    public ResponseEntity<FireStationDTO> addFireStation(@RequestBody FireStationDTO fireStationDTO) {

        FireStationDTO savedDTO = fireStationService.saveFireStation(fireStationDTO);
        log.info("FireStation at address '{}' with station number {} added successfully.",
                savedDTO.getAddress(), savedDTO.getStation());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);

    }

    /**
     * Handles HTTP PUT requests to update an existing fire station.
     * <p>
     * Accepts a FireStationDTO with updated data, delegates the update to the service layer,
     * and returns the updated fire station.
     * </p>
     *
     * @param fireStationDTO the FireStationDTO containing updated fire station information
     * @return ResponseEntity with HTTP 200 and the updated FireStationDTO
     */
    @PutMapping("/firestation")
    public ResponseEntity<FireStationDTO> updateFireStation(@RequestBody FireStationDTO fireStationDTO) {

        FireStationDTO updatedDTO = fireStationService.updateFireStation(fireStationDTO);
        log.info("FireStation at address '{}' successfully updated to station number {}.",
                updatedDTO.getAddress(), updatedDTO.getStation());

        return ResponseEntity.ok(updatedDTO);

    }

    /**
     * Deletes an existing fire station identified by its address.
     * <p>
     * This method receives the address of the fire station as a request parameter,
     * attempts to delete the corresponding fire station from the system,
     * and returns a confirmation message upon success.
     * <p>
     * If the fire station is not found, a FireStationNotFoundException is thrown
     * and handled globally by an exception handler.
     *
     * @param address the address of the fire station to delete
     * @return ResponseEntity containing:
     * - HTTP 200 OK and a success message if the fire station was deleted successfully
     */
    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestParam String address) {

        fireStationService.deleteFireStationByAddress(address);
        log.info("FireStation at address '{}' deleted successfully.", address);
        return ResponseEntity.ok("FireStation at address '" + address + "' deleted successfully.");

    }

    /**
     * Deletes all fire stations associated with a given station number.
     * <p>
     * This method receives the station number as a request parameter,
     * attempts to delete all fire stations matching this station number from the system,
     * and returns a confirmation message upon success.
     * <p>
     * If no fire stations are found with the given station number,
     * an exception may be thrown and handled globally by an exception handler.
     *
     * @param stationNumber the station number for which all fire stations should be deleted
     * @return ResponseEntity containing:
     * - HTTP 200 OK and a success message if the fire stations were deleted successfully
     */
    @DeleteMapping("/firestations")
    public ResponseEntity<String> deleteFireStationsByStationNumber(@RequestParam int stationNumber) {

        fireStationService.deleteFireStationsByStationNumber(stationNumber);
        log.info("All firestations with station number {} deleted successfully.", stationNumber);
        return ResponseEntity.ok("All firestations with station number " + stationNumber + " deleted successfully.");

    }

}
