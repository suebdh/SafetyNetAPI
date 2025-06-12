package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonInfoDto;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/persons")
    List<PersonDTO> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/person")
    public ResponseEntity<PersonDTO> getPerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            PersonDTO personDTO = personService.findByFirstNameAndLastName(firstName, lastName);
            log.info("Fetched person: {} {}", firstName, lastName);
            return ResponseEntity.ok(personDTO);
        } catch (PersonNotFoundException ex) {
            log.warn("Person {} {} not found.", firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@RequestBody PersonDTO personDTO) {
        try {
            PersonDTO saved = personService.save(personDTO);
            log.info("Person {} {} added successfully.", saved.getFirstName(), saved.getLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (PersonAlreadyExistsException ex) {
            log.warn("Cannot add person {} {}: already exists.", personDTO.getFirstName(), personDTO.getLastName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Person already exists");
        }
    }

    @PutMapping("/person")
    public ResponseEntity<?> updatePerson(@RequestBody PersonDTO personDTO) {
        try {
            PersonDTO updated = personService.update(personDTO);
            log.info("Person {} {} updated successfully.", updated.getFirstName(), updated.getLastName());
            return ResponseEntity.ok(updated);
        } catch (PersonNotFoundException ex) {
            log.warn("Cannot update person {} {}: not found.", personDTO.getFirstName(), personDTO.getLastName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found to be updated");
        }
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            personService.delete(firstName, lastName);
            log.info("Person {} {} deleted successfully.", firstName, lastName);
            return ResponseEntity.ok("Person deleted successfully.");
        } catch (PersonNotFoundException ex) {
            log.warn("Person {} {} not found, deletion impossible.", firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found, deletion impossible");
        }
    }

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
     * Handles GET requests to retrieve personal information by last name.
     *
     * @param lastName the last name to filter persons by
     * @return ResponseEntity containing a list of PersonInfoDto if found,
     * or 404 Not Found if no matching persons exist
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
