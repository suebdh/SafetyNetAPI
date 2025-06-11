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

    @PostConstruct
    public void init() {
        fireStations = new ArrayList<>(dataLoader.getDataFile().getFirestations());
    }

    @Override
    public List<FireStation> getFirestations() {
        return fireStations;
    }

    @Override
    public List<FireStation> getFirestationByStationNumber(Integer station) {
        List<FireStation> firestations = getFirestations().stream()
                .filter(fs -> fs.getStation() == station)
                .toList();
        log.debug("Found {} firestation(s) with station number {}", firestations.size(), station);
        return firestations;
    }

    @Override
    public FireStation getFirestationByAddress(String address) {
        FireStation firestation = getFirestations().stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(address))
                .findFirst()
                .orElse(null);
        log.debug("Firestation with address '{}' was {}", address, firestation != null ? "found" : "not found");
        return firestation;
    }

    @Override
    public void saveFirestation(FireStation fireStation) {
        fireStations.add(fireStation);
        log.debug("Firestation with address '{}' and station number {} saved", fireStation.getAddress(), fireStation.getStation());

    }

    @Override
    public FireStation updateFirestation(FireStation fireStation) {
        for (FireStation fs : fireStations) {
            if (fs.getAddress().equalsIgnoreCase(fireStation.getAddress())) {
                fs.setStation(fireStation.getStation());
                log.debug("Firestation at address '{}' updated with station number {}", fs.getAddress(), fs.getStation());
                return fs;
            }
        }
        log.debug("No firestation found at address '{}', update skipped", fireStation.getAddress());
        return null;

    }

    @Override
    public boolean deleteFirstOccurrenceFirestationByAddress(String address) {
        Iterator<FireStation> iterator = fireStations.iterator();
        while (iterator.hasNext()) {
            FireStation fs = iterator.next();
            if (fs.getAddress().equalsIgnoreCase(address)) {
                iterator.remove();
                log.debug("The first occurence of Firestation with address '{}' deleted", address);
                return true; // Deletion performed
            }
        }
        log.debug("No firestation found with address '{}', nothing deleted", address);
        return false; // No deletion
    }

    @Override
    public boolean deleteAllFirestationByAddress(String address) {
        boolean removed = fireStations.removeIf(fs -> fs.getAddress().equalsIgnoreCase(address));
        if (removed) {
            log.debug("All firestations with address '{}' deleted", address);
        } else {
            log.debug("No firestation at all found with address '{}', nothing deleted", address);
        }
        return removed;
    }

    @Override
    public boolean deleteByStationNumber(int stationNumber) {
        boolean removed = fireStations.removeIf(fs -> fs.getStation() == stationNumber);
        if (removed) {
            log.debug("All firestations with station number {} deleted", stationNumber);
        } else {
            log.debug("No firestations with station number {} found, nothing deleted", stationNumber);
        }
        return removed;
    }


}
