package com.openclassrooms.safetynet.safetynetapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChildDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<HouseholdMembersDTO> householdMembers;
}
