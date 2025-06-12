package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;
}
