package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AddressResidentsDTO {
    private String address;
    private List<FirePersonInfoDTO> residents;
}
