package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FireStationRepository {
    List<FireStation> getFirestations();

    List<FireStation> getFirestationByStationNumber(int station);

    FireStation getFirestationByAddress(String address);

    void saveFirestation(FireStation fireStation);

    FireStation updateFirestation(FireStation fireStation);

    boolean deleteFirstOccurrenceFirestationByAddress(String address);

    boolean deleteAllFirestationByAddress(String address);

    boolean deleteByStationNumber(int stationNumber);

}
