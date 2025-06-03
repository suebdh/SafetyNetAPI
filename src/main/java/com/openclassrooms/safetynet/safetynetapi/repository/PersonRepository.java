package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;

import java.util.List;

public interface PersonRepository {

    List<Person> findAll();
    Person save(Person person);
    Person update(Person person);
    void delete (String firstName, String lastName);
    void deleteFirstOccurrence(String firstName, String lastName);
    Person findByFirstNameAndLastName(String firstName, String lastName);
}
