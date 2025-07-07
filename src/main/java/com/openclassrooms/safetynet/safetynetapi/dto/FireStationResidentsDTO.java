package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FireStationResidentsDTO {
    private int fireStationNumber;
    private List<FirePersonInfoDTO> residents;
}
