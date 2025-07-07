package com.openclassrooms.safetynet.safetynetapi.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.openclassrooms.safetynet.safetynetapi.repository.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AlertInfoController
 *
 * <p>This class uses Spring Boot Test with MockMvc to perform HTTP requests
 * against the application's endpoints related to alert information, verifying
 * the behavior and correctness of the responses.</p>
 *
 * <p>It tests endpoints such as:</p>
 * <ul>
 *   <li>GET /communityEmail - retrieving community emails by city</li>
 *   <li>GET /personInfo - retrieving personal info by last name</li>
 *   <li>GET /phoneAlert - retrieving phone numbers by fire station</li>
 *   <li>GET /firestation - retrieving persons covered by a fire station</li>
 *   <li>GET /fire - retrieving residents and fire station info by address</li>
 *   <li>GET /childAlert - retrieving children by address</li>
 *   <li>GET /flood/stations - retrieving households served by specified fire stations</li>
 * </ul>
 *
 * <p>The tests verify expected HTTP statuses, JSON response structure, and content
 * based on the test dataset loaded before each test.</p>
 *
 * <p>The test profile test is activated to isolate test configuration and data.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class AlertInfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader.dropJsonFile();
        dataLoader.loadJsonFile();
    }

    @Test
    public void getCommunityEmails_shouldReturnEmails_whenCityExists() throws Exception {
        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(15))
                .andExpect(jsonPath("$[*]").value(containsInAnyOrder(
                        "jaboyd@email.com",
                        "drk@email.com",
                        "tenz@email.com",
                        "tcoop@ymail.com",
                        "lily@email.com",
                        "soph@email.com",
                        "ward@email.com",
                        "zarc@email.com",
                        "reg@email.com",
                        "jpeter@email.com",
                        "aly@imail.com",
                        "bstel@email.com",
                        "ssanw@email.com",
                        "clivfd@ymail.com",
                        "gramps@email.com"
                )));


    }

    @Test
    public void getCommunityEmails_shouldReturnNoContent_whenCityHasNoEmails() throws Exception {
        mockMvc.perform(get("/communityEmail")
                        .param("city", "WrongCity"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getPersonInfo_shouldReturnPersonInfo_whenLastNameExists() throws Exception {
        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(6))
                .andExpect(jsonPath("$[5].firstName").value("Allison"))
                .andExpect(jsonPath("$[5].lastName").value("Boyd"))
                .andExpect(jsonPath("$[5].age").value("60"))
                .andExpect(jsonPath("$[5].email").value("aly@imail.com"))
                .andExpect(jsonPath("$[5].address").value("112 Steppes Pl"))
                .andExpect(jsonPath("$[5].medications[0]").value("aznol:200mg"))
                .andExpect(jsonPath("$[5].allergies[0]").value("nillacilan"));
    }

    @Test
    public void getPersonInfo_shouldReturnNotFound_whenLastNameDoesNotExist() throws Exception {
        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPhoneNumberFromStation_shouldReturnPhones_whenStationExists() throws Exception {

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$[0]").value("841-874-6512"))
                .andExpect(jsonPath("$[1]").value("841-874-8547"))
                .andExpect(jsonPath("$[2]").value("841-874-7462"))
                .andExpect(jsonPath("$[3]").value("841-874-7784"));
    }

    @Test
    public void getPhoneNumberFromStation_shouldReturnNotFound_whenStationDoesNotExist() throws Exception {
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPersonsCoveredByStation_shouldReturnCoveredPersonsDTO_whenStationExists() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coveredPersons.size()").value(6))
                .andExpect(jsonPath("$.coveredPersons[0].firstName").value("Peter"))
                .andExpect(jsonPath("$.coveredPersons[0].lastName").value("Duncan"))
                .andExpect(jsonPath("$.coveredPersons[0].address").value("644 Gershwin Cir"))
                .andExpect(jsonPath("$.coveredPersons[0].phone").value("841-874-6512"))
                .andExpect(jsonPath("$.nbAdults").value(5))
                .andExpect(jsonPath("$.nbChildren").value(1));
    }

    @Test
    public void getPersonsCoveredByStation_shouldReturnNotFound_whenStationDoesNotExist() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getResidentsByAddress_shouldReturnResidentsInfo_whenAddressExists() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fireStationNumber").value(3))
                .andExpect(jsonPath("$.residents").isArray())
                .andExpect(jsonPath("$.residents.size()").value(5))
                .andExpect(jsonPath("$.residents[0].firstName").value("John"))
                .andExpect(jsonPath("$.residents[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.residents[0].phone").value("841-874-6512"))
                .andExpect(jsonPath("$.residents[0].age").value(41))
                .andExpect(jsonPath("$.residents[0].medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.residents[0].allergies[0]").value("nillacilan"));
    }

    @Test
    public void getChildrenByAddress_shouldReturnChildrenList_whenChildrenExist() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Tenley"))
                .andExpect(jsonPath("$[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].age").value(13))
        ;
    }

    @Test
    public void getChildrenByAddress_shouldReturnEmptyList_whenNoChildrenFound() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "Unknown Address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getHouseholdsByStations_shouldReturnHouseholds_whenStationsExist() throws Exception {
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))  // List of requested stations
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6)) // Expected number of addresses
                // Check the first address
                .andExpect(jsonPath("$[0].address").value("951 LoneTree Rd"))
                .andExpect(jsonPath("$[0].residents").isArray())
                .andExpect(jsonPath("$[0].residents.length()").value(1)) // number of residents for this address
                // Check details of a specific resident
                .andExpect(jsonPath("$[0].residents[0].firstName").value("Eric"))
                .andExpect(jsonPath("$[0].residents[0].lastName").value("Cadigan"))
                .andExpect(jsonPath("$[0].residents[0].age").value(79))
                .andExpect(jsonPath("$[0].residents[0].phone").value("841-874-7458"))
                .andExpect(jsonPath("$[0].residents[0].medications[0]").value("tradoxidine:400mg"))
                .andExpect(jsonPath("$[0].residents[0].allergies").isEmpty());

    }
}
