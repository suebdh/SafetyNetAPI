package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.DataFile;
import com.openclassrooms.safetynet.safetynetapi.model.Person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class InMemoryPersonRepositoryTest {

    @Mock
    private DataLoader dataLoader;

    @Mock
    private DataFile dataFile;

    @InjectMocks
    private InMemoryPersonRepository personRepository;

    @Test
    public void deleteFirstOccurrence_whenPersonExists_shouldRemovePersonAndCallSave() {
        // When getDataFile() is called, return the mocked dataFile
        when(dataLoader.getDataFile()).thenReturn(dataFile);

        Person p1 = new Person("John", "Johnson", "123 Main St", "Springfield", "12345", "123-456-7890", "john.doe@example.com");
        Person p2 = new Person("Jane", "Johnson", "456 Second St", "Springfield", "12345", "987-654-3210", "jane.doe@example.com");

        // Prepare the initial list
        List<Person> persons = new ArrayList<>(List.of(p1, p2));

        // Inject the list into the repository
        personRepository.setPersons(persons);

        // Call the method to test
        personRepository.deleteFirstOccurrence("John", "Johnson");

        // Verify that p1 has been removed
        assertFalse(persons.contains(p1));
        assertTrue(persons.contains(p2));

        // Verify that dataFile has been updated
        verify(dataFile).setPersons(persons);

        // Verify that saveJsonFile was called
        verify(dataLoader).saveJsonFile();
    }

    @Test
    public void deleteFirstOccurrence_whenPersonDoesNotExist_shouldNotModifyPersonsOrCallSave() {
        // Given: a list containing one person
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Johnson", "123 Main St", "Springfield", "12345", "123-456-7890", "john.doe@example.com"));
        personRepository.setPersons(persons);

        // When: attempting to delete a person who does not exist
        personRepository.deleteFirstOccurrence("NonExisting", "Person");

        // Then: the list should remain unchanged
        assertEquals(1, persons.size());
        assertTrue(persons.stream().anyMatch(p -> p.getFirstName().equals("John") && p.getLastName().equals("Johnson")));

        // Optional: verify that dataFile.setPersons() was not called since nothing was deleted
        verify(dataFile, never()).setPersons(any());

        // Optional: verify that saveJsonFile() was not called either
        verify(dataLoader, never()).saveJsonFile();
    }

    @Test
    public void delete_whenNonExistingPerson_shouldReturnFalseAndNotChangeData() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Johnson", "123 Main St", "Springfield", "12345", "123-456-7890", "john.doe@example.com"));
        personRepository.setPersons(persons);

        boolean result = personRepository.delete("NonExisting", "Person");

        assertFalse(result); // nothing removed
        assertEquals(1, persons.size()); // list unchanged

        // Optionally verify that dataFile.setPersons or dataLoader.saveJsonFile were NOT called
        verify(dataFile, never()).setPersons(any());
        verify(dataLoader, never()).saveJsonFile();
    }

    @Test
    public void update_whenNonExistingPerson_shouldReturnNullAndNotUpdateData() {

        // Setup list with a person different from the one we want to update
        Person existing = new Person("John", "Doe", "123 Main St", "Springfield", "12345", "123-456-7890", "john.doe@example.com");
        personRepository.setPersons(new ArrayList<>(List.of(existing)));

        // Create a person to update who is NOT in the list
        Person toUpdate = new Person("Jane", "Smith", "456 Second St", "Springfield", "12345", "987-654-3210", "jane.smith@example.com");

        // Call update with a person not in the list
        Person result = personRepository.update(toUpdate);

        // Assert that update returns null because no matching person was found
        assertNull(result);

        // Verify that dataFile.setPersons was NOT called because nothing was updated
        verify(dataFile, never()).setPersons(any());

        // Verify that saveJsonFile was NOT called either
        verify(dataLoader, never()).saveJsonFile();
    }


}
