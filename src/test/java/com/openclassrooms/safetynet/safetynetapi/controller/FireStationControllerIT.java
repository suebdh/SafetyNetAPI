package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.repository.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;//get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the FireStationController
 * This class tests all the endpoints related to fire stations, ensuring that each endpoint
 * behaves as expected by verifying the status code and the returned JSON data.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class FireStationControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader.dropJsonFile();
        dataLoader.loadJsonFile();
    }

    @Test
    public void getFireStations_shouldReturnList_whenStationsExist() throws Exception {
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
            //    .andExpect(jsonPath("$.length()").value(13))
        ;
    }

    @Test
    public void addFireStation_shouldCreateNewFireStation_andReturnIt() throws Exception {
        String fireStationJson = """
        {
            "address": "29 Baker Street",
            "station": 7
        }
    """;

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fireStationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("29 Baker Street"))
                .andExpect(jsonPath("$.station").value(7));
    }

    @Test
    public void updateFireStation_shouldUpdateExistingFireStation_andReturnUpdatedVersion() throws Exception {
        String updatedFireStationJson = """
        {
            "address": "1509 Culver St",
            "station": 5
        }
    """;

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedFireStationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value(5));
    }

    @Test
    public void deleteFireStation_shouldDeleteStation_andReturnConfirmationMessage() throws Exception {
        mockMvc.perform(delete("/firestation")
                        .param("address", "112 Steppes Pl"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("deleted successfully");
                });
    }

    @Test
    public void deleteFireStation_shouldReturnNotFound_whenAddressDoesNotExist() throws Exception {
        mockMvc.perform(delete("/firestation")
                        .param("address", "unknown address"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFireStationsByStationNumber_shouldDeleteStations_andReturnConfirmationMessage() throws Exception {
        int stationNumber = 2;

        mockMvc.perform(delete("/firestations")
                        .param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("All firestations with station number " + stationNumber + " deleted successfully.");
                });
    }

}
