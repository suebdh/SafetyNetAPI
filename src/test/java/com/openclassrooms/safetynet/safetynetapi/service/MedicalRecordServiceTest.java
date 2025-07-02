package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.service.mapper.MedicalRecordMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordService medicalRecordService;


    @Test
    public void shouldThrowException_whenGetAndMedicalRecordNotFound() {
        // Given
        String firstName = "Jad";
        String lastName = "BH";

        Mockito.when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(firstName, lastName))
                .thenReturn(null); // simulate the absence of a record

        assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName));
    }

    @Test
    public void shouldThrowMedicalRecordAlreadyExistsException_whenSaveAndRecordExists() {
        // Given
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setFirstName("Jad");
        dto.setLastName("BH");

        Mockito.when(personRepository.findByFirstNameAndLastName("Jad", "BH")).thenReturn(new Person()); // person exists
        Mockito.when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("Jad", "BH"))
                .thenReturn(new MedicalRecord()); // record exists already
        // When + Then
        assertThrows(MedicalRecordAlreadyExistsException.class, () -> medicalRecordService.saveMedicalRecord(dto));
    }

    @Test
    public void shouldThrowMedicalRecordNotFoundException_whenUpdateAndRecordNotFound() {
        //Given
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setFirstName("Jad");
        dto.setLastName("BH");

        // Mock mapping DTO to entity
        Mockito.when(medicalRecordMapper.toEntity(Mockito.any(MedicalRecordDTO.class)))
                .thenReturn(new MedicalRecord());

        // Simulate that the record exists in repository
        Mockito.when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("Jad", "BH"))
                .thenReturn(new MedicalRecord());

        // Simulate that update returns null to trigger the exception
        Mockito.when(medicalRecordRepository.updateMedicalRecord(Mockito.any()))
                .thenReturn(null);
        // When + Then
        assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.updateMedicalRecord(dto));
    }

    @Test
    public void shouldThrowMedicalRecordNotFoundException_whenDeleteAndRecordNotFound(){
        // Given: a medical record with firstName and lastName that does not exist in the repository
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setFirstName("Jad");
        dto.setLastName("BH");

        // Simulate repository behavior: delete returns false (record not found)
        Mockito.when(medicalRecordRepository.deleteMedicalRecord("Jad", "BH")).thenReturn(false);

        // When + Then: assert that the method throws MedicalRecordNotFoundException
        assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.deleteMedicalRecordByFirstNameAndLastName("Jad","BH"));
    }
}
