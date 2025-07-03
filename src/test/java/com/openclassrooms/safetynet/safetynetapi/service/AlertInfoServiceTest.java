package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.ChildDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.CoveredPersonsByStationDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.FirePersonInfoDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.FireStationResidentsDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlertInfoServiceTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FireStationRepository fireStationRepository;

    @Mock
    MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    AlertInfoService alertInfoService;

    @Test
    public void testGetResidentsByAddress_ShouldThrowFireStationNotFoundException_WhenNoFireStation() {
        String address = "123 Unknown Street";

        // Mock fireStationRepository to return null for this address
        when(fireStationRepository.getFireStationByAddress(address)).thenReturn(null);

        FireStationNotFoundException exception = assertThrows(FireStationNotFoundException.class, () -> alertInfoService.getResidentsByAddress(address));

        assertTrue(exception.getMessage().contains("No fire station found for address"));
        verify(fireStationRepository).getFireStationByAddress(address);
    }

    @Test
    void testGetResidentsByAddress_whenNoResidents() {
        String address = "NoResidents Street";

        // Mock a valid FireStation
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(5);

        when(fireStationRepository.getFireStationByAddress(address)).thenReturn(fireStation);

        // Mock an empty list of residents
        when(personRepository.getPersonByAddress(address)).thenReturn(Collections.emptyList());

        // Call the method to test
        FireStationResidentsDTO result = alertInfoService.getResidentsByAddress(address);

        // Assertions
        assertNotNull(result);
        assertEquals(5, result.getFireStationNumber());
        assertTrue(result.getResidents().isEmpty());  // The resident list should be empty as expected
    }


    @Test
    public void testBuildFirePersonInfoDTO_NoMedicalRecord() {
        // Given
        Person person = new Person();
        person.setFirstName("Jade");
        person.setLastName("BH");
        person.setPhone("123456789");

        // Simulate the absence of a medical record
        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("Jade", "BH")).thenReturn(null);

        // When
        FirePersonInfoDTO result = alertInfoService.buildFirePersonInfoDTO(person);

        // Then
        assertEquals("Jade", result.getFirstName());
        assertEquals("BH", result.getLastName());
        assertEquals("123456789", result.getPhone());
        assertEquals(-1, result.getAge());
        assertTrue(result.getMedications().isEmpty());
        assertTrue(result.getAllergies().isEmpty());

        }

    @Test
    public void testGetChildrenByAddress_WithMissingMedicalRecord() {
        // Arrange
        String address = "123 Geek Street";

        Person person1 = new Person();
        person1.setFirstName("Sara");
        person1.setLastName("Smith");

        Person person2 = new Person();
        person2.setFirstName("Bob");
        person2.setLastName("Miller");

        List<Person> personsAtAddress = Arrays.asList(person1, person2);

        // 1. Simulate retrieval of persons at the address
        when(personRepository.getPersonByAddress(address)).thenReturn(personsAtAddress);

        // 2. For person1, return a valid MedicalRecord (child)
        MedicalRecord record1 = new MedicalRecord();
        record1.setBirthdate(LocalDate.now().minusYears(10)); // 10-year-old child
        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("Sara", "Smith")).thenReturn(record1);

        // 3. For person2, simulate a missing medical record (null)
        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("Bob", "Miller")).thenReturn(null);

        // Act
        List<ChildDTO> children = alertInfoService.getChildrenByAddress(address);

        // Assert
        // We should have 1 child (Sara)
        assertEquals(1, children.size());
        ChildDTO child = children.getFirst();
        assertEquals("Sara", child.getFirstName());
        assertEquals("Smith", child.getLastName());
        assertTrue(child.getAge() <= 18);
    }

    @Test
    void testGetPersonsCoveredByStation_WithMissingMedicalRecord() {
        int stationNumber = 1;
        String address = "123 Main Avenue";

        // Mock FireStationRepository
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        when(fireStationRepository.getFireStationByStationNumber(stationNumber))
                .thenReturn(List.of(fireStation));

        // Mock PersonRepository
        Person personWithoutMedicalRecord = new Person();
        personWithoutMedicalRecord.setFirstName("John");
        personWithoutMedicalRecord.setLastName("Doe");
        personWithoutMedicalRecord.setAddress(address);
        personWithoutMedicalRecord.setPhone("123-456-7890");

        when(personRepository.getPersonByAddress(address))
                .thenReturn(List.of(personWithoutMedicalRecord));

        // Mock MedicalRecordRepository: return null (no medical record)
        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("John", "Doe"))
                .thenReturn(null);

        // Call the method under test
        CoveredPersonsByStationDTO result = alertInfoService.getPersonsCoveredByStation(stationNumber);

        // Assertions
        // The person without a medical record should be ignored → empty list
        assertTrue(result.getCoveredPersons().isEmpty());

        // Number of children and adults should be zero
        assertEquals(0, result.getNbAdults());
        assertEquals(0, result.getNbChildren());

    }




}
