package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class AlertInfoRepository implements  AlertInfoInterfaceRepository {

    @Setter
    private List<Person> persons;

    @Autowired
    private DataLoader dataLoader;

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
     * Retrieves all persons living in the specified city.
     *
     * @param city the city name to filter by (case-insensitive)
     * @return a list of persons residing in the given city; empty list if none found
     */
    @Override
    public List<Person> findByCity(String city) {
        String trimmedCity = city.trim().replaceAll("\\s+", " ");
        return persons.stream()
                .filter(person -> person.getCity().equalsIgnoreCase(trimmedCity))
                .collect(Collectors.toList());
    }
}
