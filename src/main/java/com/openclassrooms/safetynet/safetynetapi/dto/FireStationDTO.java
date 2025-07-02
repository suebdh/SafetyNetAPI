package com.openclassrooms.safetynet.safetynetapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing the mapping between a fire station number and its corresponding address.
 * <p>
 * This class is used to transfer fire station data between application layers, particularly in requests and responses related to fire station operations
 * within the SafetyNet API. It includes the fire station number and its associated address.
 * </p>
 *
 * <p>
 * A Jackson annotation is used to control the JSON serialization format of the station field, ensuring it is serialized as a string.
 * </p>
 *
 * @author Sarar
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FireStationDTO {
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int station;
}
