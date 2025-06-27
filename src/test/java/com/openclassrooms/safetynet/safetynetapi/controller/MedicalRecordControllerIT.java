package com.openclassrooms.safetynet.safetynetapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.safetynetapi.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.repository.DataLoader;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDate;
import java.util.List;

/**
 * Test class for the MedicalRecordController.
 * This class tests all the endpoints related to medical records, ensuring that each endpoint
 * behaves as expected by verifying the status code and the returned JSON data.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class MedicalRecordControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataLoader dataLoader;

    private MedicalRecordDTO medicalRecordDto;

    private PersonDTO personDto;

    @BeforeEach
    void setUp() {
        dataLoader.dropJsonFile();
        dataLoader.loadJsonFile();
    }

    @Test
    public void givenMedicalRecordsExist_whenGetAllMedicalRecords_thenReturnStatusOkAndExpectedSize() throws Exception {
        // Given - medical records are already loaded from the test dataset

        // When - performing GET request to retrieve all medical records
        mockMvc.perform(get("/medicalrecords"))
                .andDo(print()) //Prints request and response details to the console for debugging

                // Then - response should be OK and contain exactly 23 records
                .andExpect(jsonPath("$", hasSize(23)))
                .andExpect(status().isOk());
    }

    @Test
    public void getMedicalRecord_shouldReturnRecord_whenFound() throws Exception {
        // Arrange — données présentes en base ou mockées via un import de dataset
        String firstName = "Tenley";
        String lastName = "Boyd";

        // Act & Assert — exécute la requête et vérifie le contenu de la réponse JSON
        mockMvc.perform(get("/medicalrecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.medications").isArray())
                .andExpect(jsonPath("$.allergies").isArray());
    }

    @Test
    void givenValidPerson_whenAddMedicalRecord_thenReturnCreated() throws Exception {

        // GIVEN: First create a person in the system
        personDto = PersonDTO.builder()
                .firstName("Sarar")
                .lastName("HAMMAR")
                .address("9 rue revolt")
                .city("Paris")
                .zip("75014")
                .phone("005-010-1252")
                .email("hammar.sarar@gmail.com")
                .build();

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isCreated());

        // And this is the medical record corresponding to this person to be added
        medicalRecordDto = MedicalRecordDTO.builder()
                .firstName("Sarar")
                .lastName("HAMMAR")
                .birthdate(LocalDate.of(2010, 3, 6))
                .medications(List.of("aspegic:1000mg", "doliprane:10mg"))
                .allergies(List.of("peanut", "shellfish"))
                .build();

// WHEN: sending a POST request to create the medical record
        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDto)))// Converts the DTO to JSON and sets it as the request body

                // THEN: the record is created successfully with correct fields
                .andExpect(status().isCreated())
                .andDo(print()) //Prints request and response details to the console for debugging
                .andExpect(jsonPath("$.firstName").value("Sarar"))
                .andExpect(jsonPath("$.lastName").value("HAMMAR"))
                .andExpect(jsonPath("$.birthdate").value("03/06/2010"))
                .andExpect(jsonPath("$.medications[0]").value("aspegic:1000mg"))
                .andExpect(jsonPath("$.medications[1]").value("doliprane:10mg"))
                .andExpect(jsonPath("$.allergies[0]").value("peanut"))
                .andExpect(jsonPath("$.allergies[1]").value("shellfish"));
    }

    @Test
    void givenExistingMedicalRecord_whenUpdated_thenReturnUpdatedMedicalRecord() throws Exception {
        // Update the existing medical record with new medications/allergies only
        MedicalRecordDTO updatedRecordDto = MedicalRecordDTO.builder()
                .firstName("John")
                .lastName("Boyd")
                .birthdate(LocalDate.of(1984, 3, 6)) // same birthdate
                .medications(List.of("doliprane:200mg"))// medications modified
                .allergies(List.of("pollen")) //allergies modified
                .build();

        mockMvc.perform(put("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRecordDto))) // Sends updated DTO as JSON
                .andExpect(status().isOk()) // Expect 200 OK
                .andDo(print()) // Logs request and response for debugging
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.medications[0]").value("doliprane:200mg"))
                .andExpect(jsonPath("$.allergies[0]").value("pollen"));
    }

    @Test
    void givenNonExistingMedicalRecord_whenUpdate_thenReturnNotFound() throws Exception {
        // Given - an existing medical record for Sarar HAMMAR already exists in the system


        MedicalRecordDTO nonExisting = MedicalRecordDTO.builder()
                .firstName("UnknownFirstName")
                .lastName("UnknownLastName")
                .birthdate(LocalDate.of(1990, 1, 1))
                .medications(List.of("nothing"))
                .allergies(List.of("nothing"))
                .build();

        // When - sending a PUT request with the updated medical record
        mockMvc.perform(put("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExisting)))
                .andDo(print()) // Logs request and response for debugging
                // Then - the API should return 200 OK and the updated fields in the response
                .andExpect(status().isNotFound());
    }

    @Test
    void addMedicalRecordForNonExistingPerson_ShouldReturnNotFound() throws Exception {
        MedicalRecordDTO nonExistingPersonDto = MedicalRecordDTO.builder()
                .firstName("NonExistingFirstName")
                .lastName("NonExistingLastName")
                .birthdate(LocalDate.of(2000, 1, 1))
                .medications(List.of("aspirine:500mg"))
                .allergies(List.of("none"))
                .build();

        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingPersonDto)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void givenExistingMedicalRecord_whenDeleteMedicalRecord_thenReturnStatusOk() throws Exception {
        // GIVEN: A known medical record (Jonanathan Marrack) already present in the test data

        // WHEN: Delete request
        mockMvc.perform(delete("/medicalrecord")
                        .param("firstName", "Jonanathan")
                        .param("lastName", "Marrack"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}