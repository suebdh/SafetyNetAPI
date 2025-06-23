package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.ChildDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.HouseholdMembersDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.service.mapper.PersonMapper;
import com.openclassrooms.safetynet.safetynetapi.util.AgeUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
@Data
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

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




    /**
     * Retrieves a list of children (aged 18 or under) living at the specified address,
     * along with their household members.
     *
     * <p>For each person found at the address, the method attempts to retrieve their medical record
     * to determine their age. If the person is 18 years old or younger, they are considered a child.
     * The result includes their first name, last name, age, and a list of other household members
     * (excluding the child himself).</p>
     *
     * @param address the address to search for children
     * @return a list of ChildDTO objects representing each child and their household members.
     * *         Returns an empty list if no residents are found at the address or if no children live there.
     */
    public List<ChildDTO> getChildrenByAddress(String address) {

        // 1- Retrieve all persons living at the given address
        List<Person> personsAtAddress = personRepository.getPersonByAddress(address);

        if (personsAtAddress.isEmpty()) {
            log.warn("No residents found at address {}", address);
            return Collections.emptyList();
        }
        // 2- For each person:
        //    -- calculate their age using their medical record
        //    -- if age ≤ 18 → they are considered a child
        //    -- add other household members (excluding the child) to the DTO
        List<ChildDTO> children = new ArrayList<>();
        for (Person person : personsAtAddress) {
            MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (record == null) {
                log.warn("No medical record found for {} {}", person.getFirstName(), person.getLastName());
                continue;
            } // No medical record → skip
            int age = AgeUtil.calculateAge(record.getBirthdate());
            if (age <= 18) {
                ChildDTO child = new ChildDTO();
                child.setFirstName(person.getFirstName());
                child.setLastName(person.getLastName());
                child.setAge(age);

                // Household members excluding the child
                List<HouseholdMembersDTO> otherMembers = personsAtAddress.stream()
                        .filter(p -> !(p.getFirstName().equalsIgnoreCase(person.getFirstName())
                                && p.getLastName().equalsIgnoreCase(person.getLastName())))
                        .map(p -> {
                            HouseholdMembersDTO dto = new HouseholdMembersDTO();
                            dto.setFirstName(p.getFirstName());
                            dto.setLastName(p.getLastName());
                            return dto;
                        })
                        .collect(Collectors.toList());

                child.setHouseholdMembers(otherMembers);

                children.add(child);
            }
        }
        // 3- Return the list of ChildDTO
        return children;
    }
}
