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

    /**
     * Initializes the in-memory mutable list of persons.
     * This method runs after dependency injection, loading persons from the JSON data file via DataLoader.
     * It copies them into a new ArrayList to allow modifications during runtime.
     * Logs the count of persons loaded at debug level.
     */
    @PostConstruct
    public void init() {
        persons = new ArrayList<>(dataLoader.getDataFile().getPersons());
        log.debug("Persons loaded: {}", persons.size());
    }

    /**
     * Retrieves all persons stored in memory
     *
     * @return a list of all Person objects; never null but can be empty
     */
    @Override
    public List<Person> findAll() {
        log.debug("Fetching all persons. Total: {}", persons.size());
        return persons;
    }

    /**
     * Get a list of persons by address
     *
     * @param address String address of the person (case-insensitive)
     * @return List of Person objects
     */
    public List<Person> getPersonByAddress(String address) {

        List<Person> persons = findAll().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .toList();
        log.debug("{} persons with address {} found", persons.size(), address);
        return persons;
    }

    /**
     * Adds a new Person to the in-memory list and persists the updated list to the external JSON file
     *
     * @param person the Person object to add
     * @return the added Person object
     */
    @Override
    public Person save(Person person) {
        persons.add(person);
        log.debug("Person saved: {} {}, address: {}, city: {}, zip: {}, phone: {}, email: {}",
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail());
        // Update the source DataFile
        dataLoader.getDataFile().setPersons(persons);

        // Persist changes to the JSON file
        dataLoader.saveJsonFile();

        return person;
    }

    /**
     * Updates an existing Person matching the first and last name.
     * <p>
     * If a matching person is found, replaces their data with the new one,
     * updates the JSON data file accordingly, and persists the changes.
     * </p>
     *
     * @param person the Person object containing updated information
     * @return the updated Person if found and updated; otherwise, returns null
     */
    @Override
    public Person update(Person person) {
        for (int i = 0; i < persons.size(); i++) {
            Person current = persons.get(i);
            if (current.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                    current.getLastName().equalsIgnoreCase(person.getLastName())) {

                persons.set(i, person); // Update data

                log.debug("Person updated: {} {}, address={}, city={}, zip={}, phone={}, email={}",
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getCity(),
                        person.getZip(),
                        person.getPhone(),
                        person.getEmail());

                // Update DataFile
                dataLoader.getDataFile().setPersons(persons);

                // Persist to JSON
                dataLoader.saveJsonFile();

                return person;
            }
        }

        log.debug("No person found for {} {}, update skipped.",
                person.getFirstName(), person.getLastName());

        return null; // if Person not found

    }

    /**
     * Deletes all persons matching the given first and last name (case-insensitive).
     * <p>
     * If at least one match is found, removes them from the in-memory list,
     * updates the JSON data file, and persists the change.
     * </p>
     *
     * @param firstName the first name of the person(s) to delete
     * @param lastName  the last name of the person(s) to delete
     * @return true if at least one person was found and deleted; false otherwise
     */
    @Override
    public boolean delete(String firstName, String lastName) {
        boolean removed = persons.removeIf(p ->
                p.getFirstName().equalsIgnoreCase(firstName) &&
                        p.getLastName().equalsIgnoreCase(lastName)
        );

        if (removed) {
            log.debug("Person(s) with name {} {} deleted", firstName, lastName);

            // Update DataFile
            dataLoader.getDataFile().setPersons(persons);

            // Persist to JSON file
            dataLoader.saveJsonFile();
        } else {
            log.debug("No person found with name {} {}, nothing deleted", firstName, lastName);
        }

        return removed;
    }

    /**
     * Finds a person by their first and last name (case-insensitive).
     *
     * @param firstName the first name to search for
     * @param lastName the last name to search for
     * @return the matching Person if found; otherwise, null
     */
    @Override
    public Person findByFirstNameAndLastName(String firstName, String lastName) {
        for (Person person : persons) {
            if (person.getFirstName().equalsIgnoreCase(firstName) &&
                    person.getLastName().equalsIgnoreCase(lastName)) {
                return person;
            }
        }
        return null;
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

    /**
     * Deletes the first occurrence of a Person matching the given first and last name.
     * <p>
     * Only the first matching person found in the list is removed.
     * </p>
     *
     * @param firstName the first name of the person to delete
     * @param lastName the last name of the person to delete
     */
    @Override
    public void deleteFirstOccurrence(String firstName, String lastName) {
        Iterator<Person> iterator = persons.iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            Person p = iterator.next();
            if (p.getFirstName().equalsIgnoreCase(firstName) &&
                    p.getLastName().equalsIgnoreCase(lastName)) {
                iterator.remove(); // delete the first person found
                removed = true;
                break; // Exit after deleting first occurrence
            }
        }

        if (removed) {
            log.debug("First occurrence of person {} {} deleted", firstName, lastName);

            // Update the source DataFile
            dataLoader.getDataFile().setPersons(persons);

            // Persist changes to the JSON file
            dataLoader.saveJsonFile();
        } else {
            log.debug("No person found for {} {}, deletion skipped", firstName, lastName);
        }
    }
}
