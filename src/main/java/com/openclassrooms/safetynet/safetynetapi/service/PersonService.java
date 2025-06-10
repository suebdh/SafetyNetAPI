package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person save(Person person) {
        Person existing = personRepository.findByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        if (existing != null) {
            throw new PersonAlreadyExistsException("Person " + person.getFirstName() + " " + person.getLastName() + " already exists");
        }
        return personRepository.save(person);
    }

    public Person update(Person person) {
        return personRepository.update(person);
    }

    public void delete(String firstName, String lastName) {
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        if (person == null)
            throw new PersonNotFoundException("Person not found " + firstName + " " + lastName);
        else
            personRepository.delete(firstName, lastName);
    }

    public Person findByFirstNameAndLastName(String firstName, String lastName) {
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        if (person == null) {
            throw new PersonNotFoundException("Person not found: " + firstName + " " + lastName);
        }
        return person;
    }

    public void deleteFirstOccurrence(String firstName, String lastName) {
        personRepository.deleteFirstOccurrence(firstName, lastName);
    }


}
