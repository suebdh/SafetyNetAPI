package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.Data;

@Data
public class CoveredPersonsDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
