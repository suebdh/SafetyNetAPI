package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonInfoDto;
import com.openclassrooms.safetynet.safetynetapi.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller to manage Person resources.
 * <p>
 * Provides endpoints to create, read, update, and delete persons,
 * as well as additional queries like fetching emails by city or personal info by last name.
 * </p>
 */
@Log4j2
@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Retrieves the list of all persons.
     *
     * @return List of PersonDTO objects representing all persons in the system.
     */
    @GetMapping("/persons")
    public List<PersonDTO> getAllPersons() {
        return personService.getAllPersons();
    }

    /**
     * Retrieves a person by their first and last name.
     *
     * @param firstName the first name of the person
     * @param lastName  the last name of the person
     * @return ResponseEntity containing the PersonDTO if found, or 404 status if not found
     */
    @GetMapping("/person")
    public ResponseEntity<PersonDTO> getPerson(@RequestParam String firstName, @RequestParam String lastName) {
        PersonDTO personDTO = personService.findByFirstNameAndLastName(firstName, lastName);
        log.info("Fetched person: {} {}", firstName, lastName);
        return ResponseEntity.ok(personDTO);
    }

    /**
     * Adds a new person to the system.
     * <p>
     * Receives a PersonDTO in the request body, saves it via the service layer, and returns the saved person with HTTP status 201 Created.
     * <p>
     * If the person already exists, a PersonAlreadyExistsException will be thrown and handled globally.
     *
     * @param personDTO the data of the person to add
     * @return a ResponseEntity with status 201 Created and the saved person in the body
     */
    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@RequestBody PersonDTO personDTO) {
        PersonDTO saved = personService.save(personDTO);
        log.info("Person {} {} added successfully.", saved.getFirstName(), saved.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing person's information.
     * <p>
     * Receives a PersonDTO in the request body, updates it via the service layer, and returns the updated person with HTTP status 200 OK.
     * If the person does not exist, a PersonNotFoundException will be thrown and handled globally.
     * </p>
     *
     * @param personDTO the data of the person to update
     * @return a ResponseEntity with status 200 OK and the updated person in the body
     */
    @PutMapping("/person")
    public ResponseEntity<?> updatePerson(@RequestBody PersonDTO personDTO) {
        PersonDTO updated = personService.update(personDTO);
        log.info("Person {} {} updated successfully.", updated.getFirstName(), updated.getLastName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        personService.delete(firstName, lastName);
        log.info("Person {} {} deleted successfully.", firstName, lastName);
        return ResponseEntity.ok("Person " + firstName + " " + lastName + " deleted successfully.");
    }

    /**
     * Retrieves the list of email addresses for all persons residing in the specified city.
     *
     * @param city the name of the city for which to retrieve community emails
     * @return a ResponseEntity containing:
     * - HTTP 200 OK and a list of emails if any are found,
     * - HTTP 204 No Content if no emails are found for the city
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam String city) {
        List<String> emails = personService.getEmailsByCity(city);
        if (emails.isEmpty()) {
            log.info("No email found for city {}", city);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Found {} email(s) for city {}", emails.size(), city);
            return ResponseEntity.ok(emails);
        }
    }

    /**
     * Handles GET requests to retrieve personal information filtered by last name.
     *
     * @param lastName the last name used to filter persons
     * @return ResponseEntity containing:
     * - HTTP 200 OK and a list of PersonInfoDto if matching persons are found,
     * - HTTP 404 Not Found if no persons match the provided last name
     */
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfo(@RequestParam String lastName) {
        log.info("Request received for /personInfo with lastName: {}", lastName);
        List<PersonInfoDto> result = personService.getPersonInfoByLastName(lastName);

        if (result.isEmpty()) {
            log.warn("No persons found with lastName: {}", lastName);
            return ResponseEntity.notFound().build();
        }

        log.info("{} person(s) found with lastName '{}'", result.size(), lastName);
        return ResponseEntity.ok(result);

    }
}
