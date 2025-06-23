package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;

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
     * Handles HTTP GET requests to retrieve all persons in the system.
     *
     * <p>This endpoint delegates to the service layer to obtain a list of PersonDTO objects,
     * then returns them wrapped in a ResponseEntity with HTTP status 200 (OK).</p>
     *
     * <p>Logs are generated on request reception and before returning the response,
     * including the count of persons returned.</p>
     *
     * @return ResponseEntity containing the list of all PersonDTO objects and HTTP status 200 OK
     */
    @GetMapping("/persons")
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        log.info("GET request received for all persons");
        List<PersonDTO> personDTOs = personService.getAllPersons();
        log.info("Returning {} person(s)", personDTOs.size());
        return ResponseEntity.ok(personDTOs);
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
     * </p>
     * <p>
     * If the person already exists, a PersonAlreadyExistsException will be thrown and handled globally.
     * </p>
     *
     * @param personDTO the data of the person to add
     * @return a ResponseEntity with status 201 Created and the saved person in the body
     * @throws PersonAlreadyExistsException if the person already exists in the system
     */
    @PostMapping("/person")
    public ResponseEntity<PersonDTO> addPerson(@RequestBody PersonDTO personDTO) {
        PersonDTO saved = personService.save(personDTO);
        log.info("Person {} {} added successfully.", saved.getFirstName(), saved.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing person's information.
     * <p>
     * Receives a PersonDTO in the request body, updates it via the service layer, and returns
     * the updated person with HTTP status 200 OK.
     * If the person does not exist, a PersonNotFoundException will be thrown and handled globally.
     * </p>
     *
     * @param personDTO the data of the person to update
     * @return a ResponseEntity with status 200 OK and the updated PersonDTO in the body
     * @throws PersonNotFoundException if the person to update is not found
     */
    @PutMapping("/person")
    public ResponseEntity<PersonDTO> updatePerson(@RequestBody PersonDTO personDTO) {
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

}
