package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.AddressResidentsDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.CoveredPersonsByStationDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.FireStationResidentsDTO;
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
    public ResponseEntity<List<FireStation>> getFireStations() {
        log.info("GET request received for all firestations");

        List<FireStation> fireStations = fireStationService.getAllFireStations();

        if (fireStations.isEmpty()) {
            log.info("No firestations found.");
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        log.info("Returning {} firestation(s)", fireStations.size());
        return ResponseEntity.ok(fireStations);
    }

    /**
     * Handles POST requests to add a new fire station.
     *
     * @param fireStation the FireStation object to be added
     * @return ResponseEntity containing:
     * - HTTP 201 Created and the saved FireStation object upon successful creation
     */
    @PostMapping("/firestation")
    public ResponseEntity<?> addFireStation(@RequestBody FireStation fireStation) {

        FireStation saved = fireStationService.saveFireStation(fireStation);
        log.info("FireStation at address '{}' with station number {} added successfully.",
                fireStation.getAddress(), fireStation.getStation());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    /**
     * Updates the details of an existing fire station.
     * <p>
     * This method receives a FireStation object in the request body, attempts to update the corresponding fire station
     * in the system, and returns the updated entity upon success.
     *
     * @param fireStation the FireStation object containing updated information
     * @return ResponseEntity containing:
     * - HTTP 200 OK and the updated FireStation object if the update is successful
     */
    @PutMapping("/firestation")
    public ResponseEntity<?> updateFireStation(@RequestBody FireStation fireStation) {

        FireStation updated = fireStationService.updateFireStation(fireStation);
        log.info("FireStation at address '{}' successfully updated to station number {}.",
                updated.getAddress(), updated.getStation());
        return ResponseEntity.ok(updated);

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



    /**
     * GET endpoint that retrieves a list of persons covered by a given fire station number.
     * <p>
     * The response includes:
     * <ul>
     *     <li>Basic personal information (first name, last name, address, phone)</li>
     *     <li>The number of adults and children among the listed persons</li>
     * </ul>
     *
     * @param stationNumber the fire station number used to filter addresses and retrieve associated persons
     * @return a ResponseEntity containing a CoveredPersonsByStationDTO with the list of persons and counts,
     * or an appropriate HTTP error if no data is found
     */
    @GetMapping("/firestation")
    public ResponseEntity<CoveredPersonsByStationDTO> getPersonsCoveredByStation(@RequestParam("stationNumber") int stationNumber) {
        CoveredPersonsByStationDTO response = fireStationService.getPersonsCoveredByStation(stationNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint that retrieves residents and medical information for a given address.
     *
     * <p>This endpoint returns:
     * <ul>
     *   <li>The fire station number serving the specified address</li>
     *   <li>A list of residents living at that address, each including their name, phone number, age,
     *       medications, and allergies</li>
     * </ul>
     *
     * <p>Example request: <code>/fire?address=1509 Culver St</code></p>
     * <p>If no fire station is found for the given address, this endpoint will respond with an appropriate HTTP error status.</p>
     *
     * @param address the address to look up
     * @return a ResponseEntity containing a FireStationResidentsDTO with fire station number
     * and resident details; returns HTTP 200 OK on success
     */
    @GetMapping("/fire")
    public ResponseEntity<FireStationResidentsDTO> getResidentsByAddress(@RequestParam String address) {
        FireStationResidentsDTO response = fireStationService.getResidentsByAddress(address);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of households covered by the specified fire stations.
     *
     * <p>This endpoint is used in case of flooding to identify all households served by
     * one or more fire stations. For each address, it returns the list of residents living
     * there along with their personal and medical information (name, phone, age, medications, allergies).</p>
     *
     * <p>Example request: <code>/flood/stations?stations=1,2</code></p>
     *
     * @param stations the list of fire station numbers
     * @return a ResponseEntity containing a list of AddressResidentsDTO,
     *         each representing an address and its residents with medical details
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<List<AddressResidentsDTO>> getHouseholdsByStations(@RequestParam("stations") List<Integer> stations){
        List<AddressResidentsDTO> response = fireStationService.getHouseholdsByStations(stations);
        return ResponseEntity.ok(response);
    }

}
