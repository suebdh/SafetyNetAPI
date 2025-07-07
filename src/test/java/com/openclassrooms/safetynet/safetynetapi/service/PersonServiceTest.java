package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.exception.PersonNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonService personService;

    @Test
    public void ShouldThrowPersonNotFoundException_WhenFindNonExistingPerson(){
        //Given
        Mockito.when(personRepository.findByFirstNameAndLastName("Louise", "SBH")).thenReturn(null);
        //When + Then
        assertThrows(PersonNotFoundException.class, () -> personService.findByFirstNameAndLastName("Louise", "SBH"));
    }

    @Test
    public void ShouldThrowPersonNotFoundException_WhenDeleteNonExistingPerson(){

        //Given
        Mockito.when(personRepository.delete("Louise", "SBH")).thenReturn(false);
        //When + Then
        assertThrows(PersonNotFoundException.class, () -> personService.delete("Louise", "SBH"));

    }

    @Test
    public void shouldCallDeleteFirstOccurrenceOnRepository() {
        // Given
        String firstName = "Louise";
        String lastName = "SBH";

        // When
        personService.deleteFirstOccurrence(firstName, lastName);

        // Then
        Mockito.verify(personRepository).deleteFirstOccurrence(firstName, lastName);
    }
}
