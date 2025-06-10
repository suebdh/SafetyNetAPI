package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InMemoryPersonRepository implements PersonRepository {
    @Autowired
    private DataLoader dataLoader;

    @Setter
    private List<Person> persons;

    //Initialize a mutable list named persons
    @PostConstruct
    public void init() {
        persons = new ArrayList<>(dataLoader.getDataFile().getPersons());
    }

    @Override
    public List<Person> findAll() {
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
