package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.FireStationDTO;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class FireStationServiceTest {

    @Mock
    FireStationRepository fireStationRepository;

    @InjectMocks
    FireStationService fireStationService;

    @Test
    public void shouldThrowFireStationAlreadyExistsException_WhenSaveAndAlreadyExists() {
        // Given
        FireStationDTO dto = new FireStationDTO();
        dto.setAddress("New street");
        dto.setStation(40);
        //simulate the case of exists
        Mockito.when(fireStationRepository.getFireStationByAddress(dto.getAddress())).thenReturn(new FireStation());
        //When + Then
        assertThrows(FireStationAlreadyExistsException.class, () -> fireStationService.saveFireStation(dto));
    }

    @Test
    public void shouldThrowFireStationNotFoundException_whenUpdatingNonExistentFireStation(){
        // Given
        FireStationDTO dto = new FireStationDTO();
        dto.setAddress("Fake street");
        dto.setStation(42);
        //simulate the case of not found station
        Mockito.when(fireStationRepository.getFireStationByAddress(dto.getAddress())).thenReturn(null);
        //When + Then
        assertThrows(FireStationNotFoundException.class, () -> fireStationService.updateFireStation(dto));

    }

    @Test
    public void shouldFireStationNotFoundException_whenDeletingNonExistentFireStation(){

        //simulate the case of not found station
        Mockito.when(fireStationRepository.deleteByStationNumber(45)).thenReturn(false);
        //When + Then
        assertThrows(FireStationNotFoundException.class,() ->fireStationService.deleteFireStationsByStationNumber(45));
    }
}
