package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FireStationResidentsDTO {
    private int fireStationNumber;
    private List<FirePersonInfoDTO> residents;
}
