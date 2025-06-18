package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.Data;

import java.util.List;


@Data
public class CoveredPersonsByStationDTO {
    private List<CoveredPersonsDTO> coveredPersons;
    private int nbAdults;
    private int nbChildren;
}
