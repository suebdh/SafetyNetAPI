package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
@Repository
public class InMemoryFireStationRepository implements FireStationRepository {

    @Autowired
    private DataLoader dataLoader;

    private List<FireStation> fireStations;

    /**
     * Initializes the in-memory mutable list of fire stations.
     * This method runs after dependency injection, loading fire stations from the JSON data file via DataLoader.
     * It copies them into a new ArrayList to allow modifications during runtime.
     * Logs the count of fire stations loaded at debug level.
     */
    @PostConstruct
    public void init() {
        fireStations = new ArrayList<>(dataLoader.getDataFile().getFireStations());
        log.debug("Fire Stations loaded: {}", fireStations.size());
    }

    /**
     * Retrieves all fire stations stored in memory.
     *
     * @return a list of all FireStation objects; never null but can be empty
     */
    @Override
    public List<FireStation> getFireStations() {
        log.debug("Fetching all fire stations. Total: {}", fireStations.size());
        return fireStations;
    }

    /**
     * Retrieves a list of fire stations matching the given station number.
     *
     * @param station the station number to search for
     * @return a list of FireStation objects with the specified station number; never null but possibly empty
     */
    @Override
    public List<FireStation> getFireStationByStationNumber(int station) {
        List<FireStation> fireStations = getFireStations().stream()
                .filter(fs -> fs.getStation() == station)
                .toList();
        log.debug("Found {} firestation(s) with station number {}", fireStations.size(), station);
        return fireStations;
    }

    /**
     * Retrieves the fire station matching the specified address.
     *
     * @param address the address to search for (case-insensitive)
     * @return the FireStation object with the specified address, or null if none is found
     */
    @Override
    public FireStation getFireStationByAddress(String address) {
        FireStation fireStation = getFireStations().stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(address))
                .findFirst()
                .orElse(null);
        log.debug("FireStation with address '{}' was {}", address, fireStation != null ? "found" : "not found");
        return fireStation;
    }

    /**
     * Adds a new fire station to the in-memory list, updates the main data file,
     * and persists the changes to the external JSON file.
     *
     * @param fireStation the fire station to be saved
     */
    @Override
    public void saveFireStation(FireStation fireStation) {
        fireStations.add(fireStation);
        log.debug("FireStation with address '{}' and station number {} saved", fireStation.getAddress(), fireStation.getStation());

        // Update the source DataFile
        dataLoader.getDataFile().setFireStations(fireStations);

        // Persist changes to the JSON file
        dataLoader.saveJsonFile();
    }

    /**
     * Updates an existing fire station identified by its address.
     * <p>
     * If a fire station with the specified address is found, updates its station number,
     * persists the changes to the JSON data file, and returns the updated FireStation.
     * </p>
     *
     * @param fireStation the fire station containing updated information
     * @return the updated FireStation if found and updated; otherwise, returns null
     */
    @Override
    public FireStation updateFireStation(FireStation fireStation) {
        for (FireStation fs : fireStations) {
            if (fs.getAddress().equalsIgnoreCase(fireStation.getAddress())) {
                fs.setStation(fireStation.getStation());
                log.debug("FireStation at address '{}' updated with station number {}", fs.getAddress(), fs.getStation());

                // Update the source DataFile
                dataLoader.getDataFile().setFireStations(fireStations);

                // Persist changes to the JSON file
                dataLoader.saveJsonFile();

                return fs;
            }
        }
        log.debug("No firestation found at address '{}', update skipped", fireStation.getAddress());
        return null;

    }

    /**
     * Deletes the first occurrence of a fire station identified by its address.
     * <p>
     * If a fire station with the specified address is found, it is removed from the in-memory list,
     * the data file is updated accordingly, and the changes are persisted to the JSON file.
     * </p>
     *
     * @param address the address of the fire station to delete
     * @return true if a fire station was found and deleted; false otherwise
     */
    @Override
    public boolean deleteFirstOccurrenceFireStationByAddress(String address) {
        Iterator<FireStation> iterator = fireStations.iterator();
        while (iterator.hasNext()) {
            FireStation fs = iterator.next();
            if (fs.getAddress().equalsIgnoreCase(address)) {
                iterator.remove();
                log.debug("The first occurence of FireStation with address '{}' deleted", address);

                // Update source DataFile and save JSON
                dataLoader.getDataFile().setFireStations(fireStations);
                dataLoader.saveJsonFile();

                return true; // Deletion performed
            }
        }
        log.debug("No firestation found with address '{}', nothing deleted", address);
        return false; // No deletion
    }

    /**
     * Deletes all fire stations matching the specified address.
     * <p>
     * If any fire stations with the given address are found, they are removed from the in-memory list,
     * the data file is updated, and the changes are saved to the JSON file.
     * </p>
     *
     * @param address the address of the fire stations to delete
     * @return true if one or more fire stations were found and deleted; false otherwise
     */
    @Override
    public boolean deleteAllFireStationByAddress(String address) {
        boolean removed = fireStations.removeIf(fs -> fs.getAddress().equalsIgnoreCase(address));
        if (removed) {
            log.debug("All firestations with address '{}' deleted", address);

            // Update source DataFile and save JSON
            dataLoader.getDataFile().setFireStations(fireStations);
            dataLoader.saveJsonFile();
        } else {
            log.debug("No firestation at all found with address '{}', nothing deleted", address);
        }
        return removed;
    }

    /**
     * Deletes all fire stations matching the specified station number.
     * <p>
     * If any fire stations with the given station number are found, they are removed from the in-memory list,
     * the data file is updated, and the changes are saved to the JSON file.
     * </p>
     *
     * @param stationNumber the station number of the fire stations to delete
     * @return true if one or more fire stations were found and deleted; false otherwise
     */
    @Override
    public boolean deleteByStationNumber(int stationNumber) {
        boolean removed = fireStations.removeIf(fs -> fs.getStation() == stationNumber);
        if (removed) {
            log.debug("All firestations with station number {} deleted", stationNumber);

            // Update source DataFile and save JSON
            dataLoader.getDataFile().setFireStations(fireStations);
            dataLoader.saveJsonFile();
        } else {
            log.debug("No firestations with station number {} found, nothing deleted", stationNumber);
        }
        return removed;
    }

    @Override
    public List<String> getAddressesByStation(Integer stationNumber) {
        List<String> addresses = getFireStations().stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(FireStation::getAddress)
                .toList();

        log.debug("Found {} address(es) for station number {}", addresses.size(), stationNumber);
        return addresses;
    }

}
