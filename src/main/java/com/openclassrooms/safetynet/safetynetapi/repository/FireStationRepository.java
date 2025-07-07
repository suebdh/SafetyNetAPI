package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FireStationRepository {
    List<FireStation> getFireStations();

    List<FireStation> getFireStationByStationNumber(int station);

    FireStation getFireStationByAddress(String address);

    void saveFireStation(FireStation fireStation);

    FireStation updateFireStation(FireStation fireStation);

    boolean deleteFirstOccurrenceFireStationByAddress(String address);

    boolean deleteByStationNumber(int stationNumber);

    List<String> getAddressesByStation(Integer stationNumber);
}
