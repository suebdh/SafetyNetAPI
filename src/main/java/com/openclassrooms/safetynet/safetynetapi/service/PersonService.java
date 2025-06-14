package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonInfoDto;
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
import java.util.List;
import java.util.stream.Collectors;

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

    /*
    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        log.info("{} person(s) found", persons.size());

        List<PersonDTO> dtoList = new ArrayList<>();
        for (Person person : persons) {
            dtoList.add(personMapper.toDTO(person));
        }

        return dtoList;
    }*/


    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        log.info("{} fire station(s) found", persons.size());
        return persons.stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO save(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);
        Person existing = personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        if (existing != null) {
            throw new PersonAlreadyExistsException("Person " + person.getFirstName() + " " + person.getLastName() + " already exists");
        }
        Person savedPerson = personRepository.save(person);
        return personMapper.toDTO(savedPerson);
    }

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

    public void delete(String firstName, String lastName) {
        boolean removed = personRepository.delete(firstName, lastName);

        if (!removed) {
            throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
        }
    }

    public PersonDTO findByFirstNameAndLastName(String firstName, String lastName) {
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        if (person == null) {
            throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
        }
        return personMapper.toDTO(person);
    }

    public void deleteFirstOccurrence(String firstName, String lastName) {
        personRepository.deleteFirstOccurrence(firstName, lastName);
    }

    public List<String> getEmailsByCity(String city) {
        List<Person> persons = personRepository.findByCity(city);
        return persons.stream().map(Person::getEmail).distinct().collect(Collectors.toList());
    }

    /**
     * Retrieves a list of PersonInfoDto objects for all persons matching the given last name.
     * <p>
     * For each person found, this method fetches their medical record to include
     * age (calculated from birthdate), medications, and allergies.
     *
     * @param lastName the last name to search for
     * @return a list of PersonInfoDto containing personal and medical information
     */
    public List<PersonInfoDto> getPersonInfoByLastName(String lastName) {
        List<Person> persons = personRepository.findByLastName(lastName);
        List<PersonInfoDto> result = new ArrayList<>();

        for (Person person : persons) {
            MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                    person.getFirstName(), person.getLastName());

            if (record != null) {
                int age = AgeUtil.calculateAge(record.getBirthdate()); // à adapter selon ton utilitaire
                PersonInfoDto dto = new PersonInfoDto(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getEmail(),
                        age,
                        record.getMedications(),
                        record.getAllergies()
                );
                result.add(dto);
            }
        }

        return result;
    }

}
