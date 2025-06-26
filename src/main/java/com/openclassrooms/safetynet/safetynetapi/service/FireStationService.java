package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.*;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.service.mapper.FireStationMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private FireStationMapper fireStationMapper;

    /**
     * Checks if a fire station exists at the given address.
     * @param address The address to check.
     * @return true if a fire station exists, false otherwise.
     */
    private boolean fireStationExists(String address) {
        FireStation existing = fireStationRepository.getFireStationByAddress(address) ;
        return existing != null;
    }

    /**
     * Retrieves all fire stations from the repository.
     *
     * @return a list of all FireStation objects; the list may be empty if no fire stations are found.
     */
    public List<FireStationDTO> getAllFireStations() {
        List<FireStation> fireStations = fireStationRepository.getFireStations();
        log.info("{} fire station(s) found", fireStations.size());
        return fireStationMapper.toDtoList(fireStations);
    }

    /**
     * Saves a new fire station to the system.
     * <p>
     * Converts the given FireStationDTO to an entity and persists it if no fire station
     * already exists at the specified address.
     * Throws a FireStationAlreadyExistsException if a fire station is already registered at that address.
     * </p>
     *
     * @param fireStationDTO the FireStationDTO containing the address and station number to be saved
     * @return the saved FireStationDTO
     * @throws FireStationAlreadyExistsException if a fire station already exists at the given address
     */
    public FireStationDTO saveFireStation(FireStationDTO fireStationDTO) {
        log.info("Request received to save firestation at address '{}' with station number {}",
                fireStationDTO.getAddress(), fireStationDTO.getStation());

        if (fireStationExists(fireStationDTO.getAddress())) {
            log.error("FireStation already exists at address '{}'", fireStationDTO.getAddress());
            throw new FireStationAlreadyExistsException("FireStation already exists at address: " + fireStationDTO.getAddress());
        }

        // Conversion DTO → Entity
        FireStation fireStation = fireStationMapper.toEntity(fireStationDTO);
        // Persistence
        fireStationRepository.saveFireStation(fireStation);

        log.info("FireStation saved successfully at address '{}' with station number {}",
                fireStation.getAddress(), fireStation.getStation());

        return fireStationMapper.toDTO(fireStation);
    }

    /**
     * Updates the information of an existing fire station.
     * <p>
     * Searches for a fire station by address and updates its station number if found.
     * If no fire station exists at the given address, a FireStationNotFoundException is thrown.
     * </p>
     *
     * @param fireStationDTO the FireStation DTO object with the updated data
     * @return the updated FireStation entity
     * @throws FireStationNotFoundException if no fire station exists at the specified address
     */

    public FireStationDTO updateFireStation(FireStationDTO fireStationDTO) {
        log.info("Request received to update firestation at address '{}'", fireStationDTO.getAddress());

        if (!fireStationExists(fireStationDTO.getAddress())) {

            log.error("No firestation found at address '{}', cannot update", fireStationDTO.getAddress());
            throw new FireStationNotFoundException("No firestation found at address: " + fireStationDTO.getAddress());
        }
        // Conversion DTO → Entity
        FireStation fireStation = fireStationMapper.toEntity(fireStationDTO);
        // Persistence
        FireStation updated = fireStationRepository.updateFireStation(fireStation);

        log.info("FireStation at address '{}' updated to station number {}", updated.getAddress(), updated.getStation());
        return fireStationMapper.toDTO(updated);
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
    public void deleteFireStationByAddress(String address) {
        log.info("Request received to delete firestation at address '{}'", address);

        boolean deleted = fireStationRepository.deleteFirstOccurrenceFireStationByAddress(address);

        if (deleted) {
            log.info("FireStation at address '{}' successfully deleted", address);
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
    public void deleteFireStationsByStationNumber(int stationNumber) {
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
