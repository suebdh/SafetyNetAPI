package com.openclassrooms.safetynet.safetynetapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.repository.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test class for the PersonController
 * This class tests all the endpoints related to persons, ensuring that each endpoint
 * behaves as expected by verifying the status code and the returned JSON data.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class PersonControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader.dropJsonFile();
        dataLoader.loadJsonFile();
    }

    @Test
    public void getAllPersons_shouldReturnListOfPersons_whenDataExists() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThan(0))); // check that there is at least one person
    }

    @Test
    public void givenExistingPerson_whenGetPerson_thenReturnPerson() throws Exception {
        //Arrange
        String firstName = "John";
        String lastName = "Boyd";

        // Act & Assert
        mockMvc.perform(get("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
        ;
    }


    @Test
    public void givenExistingPerson_whenAddPerson_thenReturnConflict() throws Exception {
        // GIVEN: A person already existing in the data (eg: Jacob Boyd)
        PersonDTO existingPerson = PersonDTO.builder()
                .firstName("Jacob")
                .lastName("Boyd")
                .address("1509 Culver St")
                .city("Culver")
                .zip("97451")
                .phone("841-874-6513")
                .email("drk@email.com")
                .build();

        // WHEN : Trying to create this person again
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingPerson)))
                .andDo(print())
                // THEN : The status should be 409 Conflict
                .andExpect(status().isConflict());
    }
        @Test
        public void givenValidPerson_whenAddPerson_thenReturnCreated () throws Exception {
            // GIVEN: A new person to add
            PersonDTO personDto = PersonDTO.builder()
                    .firstName("Sue")
                    .lastName("BDH")
                    .address("9 rue revolt")
                    .city("Paris")
                    .zip("75018")
                    .phone("005-016-1872")
                    .email("sue.bdh@yahoo.fr")
                    .build();

            // WHEN: Sending a POST request to create the person

            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(personDto)))
                    .andDo(print()) // Log des détails pour debug
                    // THEN: The person is successfully created (201) with the correct data
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("Sue"))
                    .andExpect(jsonPath("$.lastName").value("BDH"))
                    .andExpect(jsonPath("$.address").value("9 rue revolt"))
                    .andExpect(jsonPath("$.city").value("Paris"))
                    .andExpect(jsonPath("$.zip").value("75018"))
                    .andExpect(jsonPath("$.phone").value("005-016-1872"))
                    .andExpect(jsonPath("$.email").value("sue.bdh@yahoo.fr"));
        }

    @Test
    public void givenExistingPerson_whenUpdatePerson_thenReturnUpdated() throws Exception {
        // Step 1: Add an initial person
        String initialJson = """
        {
          "firstName": "Jane",
          "lastName": "Doe",
          "address": "123 Old Street",
          "city": "OldTown",
          "zip": "11111",
          "phone": "111-111-1111",
          "email": "jane.doe@example.com"
        }
        """;

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initialJson))
                .andExpect(status().isCreated());

        // Step 2: Prepare the updated data
        String updatedJson = """
        {
          "firstName": "Jane",
          "lastName": "Doe",
          "address": "456 New Street",
          "city": "NewTown",
          "zip": "22222",
          "phone": "222-222-2222",
          "email": "jane.doe@updated.com"
        }
        """;

        // Step 3: Send PUT request for update
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("456 New Street"))
                .andExpect(jsonPath("$.city").value("NewTown"))
                .andExpect(jsonPath("$.zip").value("22222"))
                .andExpect(jsonPath("$.phone").value("222-222-2222"))
                .andExpect(jsonPath("$.email").value("jane.doe@updated.com"));
    }

    @Test
    public void givenNonExistingPerson_whenUpdatePerson_thenReturnNotFound() throws Exception {
        String json = """
        {
          "firstName": "NonExistingFirstName",
          "lastName": "NonExistingLastName",
          "address": "Nowhere",
          "city": "NoCity",
          "zip": "00000",
          "phone": "000-000-0000",
          "email": "nonexisting@nowhere.com"
        }
        """;

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenExistingPerson_whenDeletePerson_thenReturnOk() throws Exception {
        // Step 1: Create a person
        String jsonPerson = """
        {
          "firstName": "Mark",
          "lastName": "Zuckerberg",
          "address": "12 Elm Street",
          "city": "New York",
          "zip": "12345",
          "phone": "123-456-7890",
          "email": "mark.zuckerberg@hotmail.com"
        }
        """;

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPerson))
                .andExpect(status().isCreated());

        // Step 2: Delete this person
        mockMvc.perform(delete("/person")
                        .param("firstName", "Mark")
                        .param("lastName", "Zuckerberg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Person Mark Zuckerberg deleted successfully."));
    }
    }
