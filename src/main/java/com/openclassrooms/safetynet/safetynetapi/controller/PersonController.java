package com.openclassrooms.safetynet.safetynetapi.controller;

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

    @PostMapping("/person")  // Mapper la méthode sur l’URL /person et la méthode HTTP POST
    public Person addPerson(@RequestBody Person person) {  // Récupérer le JSON de la requête, converti en objet Person
        return personService.save(person);  // Appeler le service pour sauvegarder la personne et Renvoyer la personne sauvegardée
    }

    @PutMapping("/person")
    public Person updatePerson(@RequestBody Person person) {
        return personService.update(person);
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
