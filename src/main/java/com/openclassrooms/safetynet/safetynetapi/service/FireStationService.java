package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing fire station data and operations.
 * <p>
 * This service interacts with the FireStationRepository to perform CRUD operations
 * and contains business logic related to fire stations, such as validation
 * and exception handling for non-existing or duplicate fire stations.
 * </p>
 */
@Log4j2
@Service
@Data
public class FireStationService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FireStationRepository fireStationRepository;

    /**
     * Retrieves all fire stations from the repository.
     *
     * @return a list of all FireStation objects; the list may be empty if no fire stations are found.
     */
    public List<FireStation> getAllFireStations() {
        List<FireStation> firestations = fireStationRepository.getFirestations();
        log.info("{} fire station(s) found", firestations.size());
        return firestations;
    }

    /**
     * Retrieves a fire station by its address.
     * <p>
     * This method searches for a fire station matching the provided address.
     * If a fire station is found, it is returned; otherwise, a
     * {@link FireStationNotFoundException} is thrown.
     * </p>
     *
     * @param address the address of the fire station to retrieve
     * @return the FireStation found at the specified address
     * @throws FireStationNotFoundException if no fire station is found at the given address
     */
    public FireStation getFirestationByAddress(String address) {
        log.info("Request received to find firestation by address: '{}'", address);

        FireStation firestation = fireStationRepository.getFirestationByAddress(address);

        if (firestation == null) {
            log.error("No firestation found at address '{}'", address);
            throw new FireStationNotFoundException("No firestation found at address: " + address);
        }

        log.info("Firestation found at address '{}'", address);
        return firestation;
    }

    /**
     * Updates an existing fire station's information.
     * <p>
     * This method attempts to update the fire station associated with the given address.
     * If no matching fire station is found, a FireStationNotFoundException is thrown.
     *
     * @param firestation the FireStation object containing updated address and station number
     * @return the updated FireStation object
     * @throws FireStationNotFoundException if no fire station is found at the specified address
     */
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

    /**
     * Saves a new fire station to the system.
     * <p>
     * This method stores the provided FireStation object if no fire station already exists at the specified address.
     * If a fire station is already present, a FireStationAlreadyExistsException is thrown.
     *
     * @param firestation the FireStation object to be saved
     * @return the saved FireStation object
     * @throws FireStationAlreadyExistsException if a fire station already exists at the given address
     */
    public FireStation saveFirestation(FireStation firestation) {
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

        return firestation;
    }

    /**
     * Deletes a fire station identified by its address.
     * <p>
     * This method attempts to delete the first occurrence of a fire station at the specified address.
     * If no fire station is found at the given address, a FireStationNotFoundException is thrown.
     *
     * @param address the address of the fire station to delete
     * @throws FireStationNotFoundException if no fire station is found at the specified address
     */
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

    /**
     * Deletes all fire stations associated with the given station number.
     * <p>
     * If no fire stations are found with the specified station number, a FireStationNotFoundException is thrown.
     *
     * @param stationNumber the station number whose associated fire stations should be deleted
     * @throws FireStationNotFoundException if no fire stations are found with the given station number
     */
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

    /**
     * Retrieves a list of unique phone numbers for all persons covered by a given fire station number.
     *
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Fetches all fire stations associated with the provided station number.</li>
     *   <li>Extracts the addresses covered by these fire stations.</li>
     *   <li>Retrieves all persons living at those addresses.</li>
     *   <li>Extracts and returns a list of distinct phone numbers from these persons.</li>
     * </ol>
     *
     * @param stationNumber the fire station number to search for
     * @return a list of unique phone numbers of persons covered by the fire station
     * @throws FireStationNotFoundException if no fire stations are found for the given station number
     */
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // 1. Retrieve all fire stations with this station number
        List<FireStation> fireStations = fireStationRepository.getFirestationByStationNumber(stationNumber);
        if (fireStations.isEmpty()) {
            throw new FireStationNotFoundException("Fire station(s) with station number" + stationNumber + " are not found.");
        }

        // 2. Extract the addresses of these fire stations
        List<String> addresses = fireStations.stream()
                .map(FireStation::getAddress)
                .toList();

        log.info("Station number {} covers {} addresses", stationNumber, addresses.size());

        // 3. Retrieve all persons living at these addresses
        List<Person> personsCovered = new ArrayList<>();
        for (String address : addresses) {
            personsCovered.addAll(personRepository.getPersonByAddress(address));
        }
        log.debug("Found {} persons covered by this station", personsCovered.size());


        // 4. Extract unique phone numbers
        List<String> phoneNumbers = personsCovered.stream()
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());

        log.debug("Returning {} unique phone numbers", phoneNumbers.size());
        return phoneNumbers;
    }


}
