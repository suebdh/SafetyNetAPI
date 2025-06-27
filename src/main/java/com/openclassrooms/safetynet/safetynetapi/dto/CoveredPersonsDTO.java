package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoveredPersonsDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
