package com.openclassrooms.safetynet.safetynetapi.service.mapper;

import com.openclassrooms.safetynet.safetynetapi.dto.FireStationDTO;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Mapper service for converting between FireStation entities and FireStationDTO data transfer objects.
 * <p>
 * Provides methods to convert individual objects as well as lists of entities and DTOs.
 * Handles null inputs (NPE) by returning null or empty lists as appropriate.
 * </p>
 *
 * <p>
 * This mapper provides the following conversions:
 * <ul>
 *     <li><code>toDTO(...)</code> converts an entity to a DTO for use in controllers or API responses.</li>
 *     <li><code>toEntity(...)</code> converts a DTO to an entity for persistence or business logic processing.</li>
 *     <li><code>toDtoList(...)</code> converts a list of entities to a list of DTOs.</li>
 *     <li><code>toEntityList(...)</code> converts a list of DTOs to a list of entities.</li>
 * </ul>
 * <p>
 * This class is intended to be injected where mapping between FireStation and FireStationDTO is needed.
 *
 * @author Sarar
 */
@Service
public class FireStationMapper {

    public FireStationDTO toDTO(FireStation fireStation){
        if (fireStation == null){
            return null;
        }
        return FireStationDTO.builder()
                .address(fireStation.getAddress())
                .station(fireStation.getStation())
                .build();
    }

    public FireStation toEntity(FireStationDTO dto){
        if (dto == null) {
            return null;
        }
        return new FireStation(dto.getAddress(), dto.getStation());
    }

    public List<FireStationDTO> toDtoList(List<FireStation> fireStations){
        if (fireStations == null) return List.of();
        return fireStations.stream().map(this::toDTO).toList();
    }

    public List<FireStation> toEntityList (List<FireStationDTO> dtos){
        if (dtos == null) return List.of();
        return dtos.stream().map(this::toEntity).toList();
    }
}
