package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        personService.delete(firstName, lastName);
    }

}
