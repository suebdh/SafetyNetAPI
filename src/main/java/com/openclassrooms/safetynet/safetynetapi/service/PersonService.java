package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.service.mapper.PersonMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Person entities.
 * <p>
 * Provides methods to create, read, update, and delete persons,
 * as well as to fetch additional information like emails by city
 * and detailed personal info by last name.
 * <p>
 * Handles business logic and throws specific exceptions like PersonNotFoundException
 * and PersonAlreadyExistsException when appropriate.
 */
@Log4j2
@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    /**
     * Retrieves all persons from the repository.
     *
     * @return a list of PersonDTO objects representing all persons in the system
     */
    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        log.info("{} fire station(s) found", persons.size());
        return persons.stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a person by their first and last name.
     * <p>
     * Throws PersonNotFoundException if no matching person is found.
     *
     * @param firstName the first name of the person to retrieve
     * @param lastName  the last name of the person to retrieve
     * @return the corresponding PersonDTO
     * @throws PersonNotFoundException if the person is not found
     */
    public PersonDTO findByFirstNameAndLastName(String firstName, String lastName) {
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        if (person == null) {
            throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
        }
        return personMapper.toDTO(person);
    }

    /**
     * Saves a new person in the repository.
     * <p>
     * Throws PersonAlreadyExistsException if a person with the same first and last name already exists.
     *
     * @param personDTO the person data to save
     * @return the saved PersonDTO object
     * @throws PersonAlreadyExistsException if the person already exists
     */
    public PersonDTO save(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        Person existing = personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        if (existing != null) {
            throw new PersonAlreadyExistsException("Person " + person.getFirstName() + " " + person.getLastName() + " already exists");
        }
        Person savedPerson = personRepository.save(person);
        return personMapper.toDTO(savedPerson);
    }

    /**
     * Updates an existing person in the repository.
     * <p>
     * Throws PersonNotFoundException if the person to update does not exist.
     *
     * @param personDTO the person data to update
     * @return the updated PersonDTO object
     * @throws PersonNotFoundException if the person does not exist
     */
    public PersonDTO update(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        Person existing = personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        if (existing == null) {
            throw new PersonNotFoundException("Cannot update, person not found: " +
                    person.getFirstName() + " " + person.getLastName());
        }
        Person updatedPerson = personRepository.update(person);
        return personMapper.toDTO(updatedPerson);
    }

    /**
     * Deletes a person identified by first name and last name.
     * <p>
     * Throws PersonNotFoundException if no matching person is found to delete.
     *
     * @param firstName the first name of the person to delete
     * @param lastName  the last name of the person to delete
     * @throws PersonNotFoundException if the person to delete does not exist
     */
    public void delete(String firstName, String lastName) {
        boolean removed = personRepository.delete(firstName, lastName);

        if (!removed) {
            throw new PersonNotFoundException("Cannot delete, Person not found : " + firstName + " " + lastName);
        }
    }

    public void deleteFirstOccurrence(String firstName, String lastName) {
        personRepository.deleteFirstOccurrence(firstName, lastName);
    }

}
