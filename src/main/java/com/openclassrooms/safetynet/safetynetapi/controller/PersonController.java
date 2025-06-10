package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.exception.PersonAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
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
    List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/person")
    public ResponseEntity<Person> getPerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            Person person = personService.findByFirstNameAndLastName(firstName, lastName);
            log.info("Fetched person: {} {}", firstName, lastName);
            return ResponseEntity.ok(person);
        } catch (PersonNotFoundException ex) {
            log.warn("Person {} {} not found.", firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/person")
    public ResponseEntity<?> addPerson(@RequestBody Person person) {
        try {
            Person saved = personService.save(person);
            log.info("Person {} {} added successfully.", person.getFirstName(), person.getLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (PersonAlreadyExistsException ex) {
            log.warn("Cannot add person {} {}: already exists.", person.getFirstName(), person.getLastName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Person already exists");
        }
    }

    @PutMapping("/person")
    public ResponseEntity<?>  updatePerson(@RequestBody Person person) {
        try {
            Person updated = personService.update(person);
            log.info("Person {} {} updated successfully.", person.getFirstName(), person.getLastName());
            return ResponseEntity.ok(updated);
        } catch (PersonNotFoundException ex) {
            log.warn("Cannot update person {} {}: not found.", person.getFirstName(), person.getLastName());
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
}
