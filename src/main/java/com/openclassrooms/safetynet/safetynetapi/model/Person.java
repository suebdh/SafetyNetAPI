package com.openclassrooms.safetynet.safetynetapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a person with personal and contact details.
 *
 * <p>This class contains typical attributes such as first name, last name,
 * address, city, postal code, phone number, and email address.</p>
 *
 * <p>The class uses Lombok's @Data annotation to automatically generate
 * getters, setters, equals, hashCode, and toString methods.</p>
 *
 * @author [Sarar]
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}

