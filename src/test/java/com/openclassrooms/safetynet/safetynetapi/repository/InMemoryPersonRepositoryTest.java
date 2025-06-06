package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPersonRepositoryTest {

    // Repository utilisé dans tous les tests
    private InMemoryPersonRepository repository;

    /**
     * Initialise le repository avec une liste vide avant chaque test.
     */
    @BeforeEach
    void setUp() {
        // GIVEN : une instance vide du repository
        repository = new InMemoryPersonRepository();
        repository.setPersons(new ArrayList<>());// Initialize the list as empty
    }

    @Test
    void findAll() {
        // WHEN : on appelle findAll()
        List<Person> allPersons = repository.findAll();

        // THEN : la liste est bien vide
        assertNotNull(allPersons);
        assertTrue(allPersons.isEmpty());

        // Optionnel : on peut ajouter une personne et vérifier que findAll la retourne
        Person person = new Person();
        person.setFirstName("Louise");
        person.setLastName("Wonderland");
        repository.save(person);

        allPersons = repository.findAll();
        assertEquals(1, allPersons.size());
        assertEquals("Louise", allPersons.getFirst().getFirstName());
    }

    @Test
    void save() {
        // WHEN : on ajoute une personne
        Person person = new Person();
        person.setFirstName("Nicolas");
        person.setLastName("Travolta");
        repository.save(person);

        // THEN : la personne doit être présente dans la liste
        assertEquals(1, repository.findAll().size());
        assertEquals("Nicolas", repository.findAll().getFirst().getFirstName());
    }

    @Test
    void update() {
        // GIVEN : une personne déjà enregistrée dans le repository
        Person person = new Person();
        person.setFirstName("Nicolas");
        person.setLastName("Travolta");
        person.setAddress("Old Address");
        repository.save(person);

        // WHEN : on modifie l'adresse de Nicolas Travolta
        Person updatedPerson = new Person();
        updatedPerson.setFirstName("Nicolas");
        updatedPerson.setLastName("Travolta");
        updatedPerson.setAddress("New Address");

        Person result = repository.update(updatedPerson);

        // THEN : la personne est mise à jour et retournée
        assertNotNull(result);
        assertEquals("New Address", repository.findAll().getFirst().getAddress());

        // WHEN : on tente de mettre à jour une personne inexistante
        Person nonExistent = new Person();
        nonExistent.setFirstName("Joelle");
        nonExistent.setLastName("Smith");

        Person resultNull = repository.update(nonExistent);

        // THEN : la méthode retourne null
        assertNull(resultNull);
    }

    @Test
    void delete() {
        // GIVEN : un repository avec plusieurs personnes (dont deux "Nicolas Travolta")
        repository = getInMemoryPersonRepository();

        // WHEN : on supprime toutes les personnes "Nicolas Travolta"
        repository.delete("Nicolas", "Travolta");

        // THEN : toutes les personnes "Nicolas Travolta" doivent être supprimées
        List<Person> remaining = repository.findAll();

        assertEquals(1, remaining.size());
        assertEquals("Joelle", remaining.getFirst().getFirstName());
        assertEquals("Travolta", remaining.getFirst().getLastName());

        // WHEN : on tente de supprimer une personne inexistante
        repository.delete("Nonexistent", "Person");

        // THEN : la liste ne change pas
        assertEquals(1, repository.findAll().size());
    }

    private static InMemoryPersonRepository getInMemoryPersonRepository() {
        InMemoryPersonRepository repository = new InMemoryPersonRepository();
        repository.setPersons(new ArrayList<>());

        Person person1 = new Person();
        person1.setFirstName("Nicolas");
        person1.setLastName("Travolta");

        Person person2 = new Person();
        person2.setFirstName("Joelle");
        person2.setLastName("Travolta");

        Person person3 = new Person();
        person3.setFirstName("Nicolas");
        person3.setLastName("Travolta");

        repository.save(person1);
        repository.save(person2);
        repository.save(person3);
        return repository;
    }

    @Test
    void deleteFirstOccurrence() {
        // GIVEN : un repository avec plusieurs personnes
        Person person1 = new Person();
        person1.setFirstName("Nicolas");
        person1.setLastName("Travolta");

        Person person2 = new Person();
        person2.setFirstName("Joelle");
        person2.setLastName("Travolta");

        Person person3 = new Person();
        person3.setFirstName("Nicolas");
        person3.setLastName("Travolta");

        repository.save(person1);
        repository.save(person2);
        repository.save(person3);

        // WHEN : on supprime la première occurrence de "Nicolas Travolta"
        repository.deleteFirstOccurrence("Nicolas", "Travolta");

        // THEN : une seule "Nicolas Travolta" doit être supprimée
        List<Person> remaining = repository.findAll();

        // Il doit rester 2 personnes : "Joelle Travolta" et 1 "Nicolas Travolta"
        assertEquals(2, remaining.size());

        // Vérifions qu’il reste bien un "Nicolas Travolta"
        long countNicolasTravolta = remaining.stream()
                .filter(p -> p.getFirstName().equals("Nicolas") && p.getLastName().equals("Travolta"))
                .count();

        assertEquals(1, countNicolasTravolta);

        // WHEN : on tente de supprimer une personne inexistante
        repository.deleteFirstOccurrence("Nonexistent", "Person");

        // THEN : la liste ne change pas
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void findByFirstNameAndLastName() {
        // GIVEN : un repository avec plusieurs personnes
       Person person1 = new Person();
        person1.setFirstName("Nicolas");
        person1.setLastName("Travolta");

        Person person2 = new Person();
        person2.setFirstName("Joelle");
        person2.setLastName("Smith");

        repository.save(person1);
        repository.save(person2);

        // WHEN : on cherche "Nicolas Travolta"
        Person found = repository.findByFirstNameAndLastName("Nicolas", "Travolta");

        // THEN : on trouve la bonne personne
        assertNotNull(found);
        assertEquals("Nicolas", found.getFirstName());
        assertEquals("Travolta", found.getLastName());

        // WHEN : on cherche une personne inexistante
        Person notFound = repository.findByFirstNameAndLastName("Louise", "Wonderland");

        // THEN : on obtient null
        assertNull(notFound);
    }
}