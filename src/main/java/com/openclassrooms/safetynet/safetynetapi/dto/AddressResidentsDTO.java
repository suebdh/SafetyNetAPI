package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AddressResidentsDTO {
    private String address;
    private List<FirePersonInfoDTO> residents;
}
