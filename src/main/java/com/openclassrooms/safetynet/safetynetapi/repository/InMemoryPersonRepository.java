package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class InMemoryPersonRepository implements PersonRepository {
    @Autowired
    private DataLoader dataLoader;

    @Setter
    private List<Person> persons;

    //Initialize a mutable list named persons
    @PostConstruct
    public void init() {
        persons = new ArrayList<>(dataLoader.getDataFile().getPersons());
        log.debug("Persons loaded: {}", persons.size());
    }

    @Override
    public List<Person> findAll() {
        log.debug("Fetching all persons. Total: {}", persons.size());
        return persons;
    }

    //add new Person in persons
    @Override
    public Person save(Person person) {
        persons.add(person);
        return person;
    }

    @Override
    public Person update(Person person) {
        for (int i = 0; i < persons.size(); i++) {
            Person current = persons.get(i);
            if (current.getFirstName().equals(person.getFirstName()) &&
                    current.getLastName().equals(person.getLastName())) {

                persons.set(i, person); // Update data
                return person;
            }
        }
        return null; // if Person not found

    }

    //removeIf removes ALL people who match this first name and last name
    @Override
    public void delete(String firstName, String lastName) {
        persons.removeIf(p ->
                p.getFirstName().equals(firstName) &&
                        p.getLastName().equals(lastName)
        );
    }


    @Override
    public Person findByFirstNameAndLastName(String firstName, String lastName) {
        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) &&
                    person.getLastName().equals(lastName)) {
                return person;
            }
        }
        return null;
    }

    @Override
    public List<Person> findByCity(String city) {
        return persons.stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    /**
     * Finds all persons whose last name matches the given lastName (case-insensitive).
     *
     * @param lastName the last name to search for
     * @return a list of matching Person objects; empty list if none found
     */
    @Override
    public List<Person> findByLastName(String lastName) {
        return persons.stream().
                filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFirstOccurrence(String firstName, String lastName) {
        Iterator<Person> iterator = persons.iterator();
        while (iterator.hasNext()) {
            Person p = iterator.next();
            if (p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)) {
                iterator.remove(); // delete the first person found
                break; // Exit the loop once the person is deleted
            }
        }
    }
}
