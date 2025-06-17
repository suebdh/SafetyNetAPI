package com.openclassrooms.safetynet.safetynetapi.dto;


import lombok.Data;

import java.util.List;

@Data
public class ChildDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<HouseholdMembersDTO> householdMembers;
}
