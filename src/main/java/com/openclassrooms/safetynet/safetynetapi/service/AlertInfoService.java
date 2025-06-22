package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.AlertInfoInterfaceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AlertInfoService {

    @Autowired
    private AlertInfoInterfaceRepository alertInfoInterfaceRepository;

    /**
     * Retrieves a list of unique email addresses of all persons living in the specified city.
     * <p>
     * This method filters persons by city, extracts their emails, and removes duplicates.
     * </p>
     *
     * @param city the name of the city for which to retrieve email addresses
     * @return a list of distinct email addresses of residents in the given city
     */
    public List<String> getEmailsByCity(String city) {
        List<Person> persons = alertInfoInterfaceRepository.findByCity(city);
        return persons.stream().map(Person::getEmail).distinct().collect(Collectors.toList());
    }
}
