package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoveredPersonsByStationDTO {
    private List<CoveredPersonsDTO> coveredPersons;
    private int nbAdults;
    private int nbChildren;
}
