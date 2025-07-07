package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PersonInfoDto {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}
