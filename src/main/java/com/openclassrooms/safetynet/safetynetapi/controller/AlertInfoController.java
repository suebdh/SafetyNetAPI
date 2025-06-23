package com.openclassrooms.safetynet.safetynetapi.controller;

import com.openclassrooms.safetynet.safetynetapi.dto.ChildDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.PersonInfoDto;
import com.openclassrooms.safetynet.safetynetapi.service.AlertInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
public class AlertInfoController {

    @Autowired
    private AlertInfoService alertInfoService;


    /**
     * Retrieves the list of email addresses for all persons residing in the specified city.
     *
     * @param city the name of the city for which to retrieve community emails
     * @return a ResponseEntity containing:
     * - HTTP 200 OK and a list of emails if any are found,
     * - HTTP 204 No Content if no emails are found for the city
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam String city) {
        List<String> emails = alertInfoService.getEmailsByCity(city);
        if (emails.isEmpty()) {
            log.info("No email found for city {}", city);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Found {} email(s) for city {}", emails.size(), city);
            return ResponseEntity.ok(emails);
        }
    }


    /**
     * Handles GET requests to retrieve personal information filtered by last name.
     *
     * @param lastName the last name used to filter persons
     * @return ResponseEntity containing:
     * - HTTP 200 OK and a list of PersonInfoDto if matching persons are found,
     * - HTTP 404 Not Found if no persons match the provided last name
     */
    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfo(@RequestParam String lastName) {
        log.info("Request received for /personInfo with lastName: {}", lastName);
        List<PersonInfoDto> result = alertInfoService.getPersonInfoByLastName(lastName);

        if (result.isEmpty()) {
            log.warn("No persons found with lastName: {}", lastName);
            return ResponseEntity.notFound().build();
        }

        log.info("{} person(s) found with lastName '{}'", result.size(), lastName);
        return ResponseEntity.ok(result);

    }

    /**
     * Handles GET requests to retrieve a list of children living at a specified address.
     *
     * <p>Expects a query parameter "address" representing the address to search.
     * Calls the service layer to get the children and their household members residing at the address.</p>
     *
     * <p>If no children are found at the given address, returns HTTP 204 No Content.</p>
     *
     * @param address the address to search for children
     * @return a ResponseEntity containing:
     *         - HTTP 200 OK and a list of ChildDTO if children are found,
     *         - HTTP 204 No Content if no children live at the specified address
     */
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildDTO>> getChildrenByAddress(@RequestParam String address){
        log.info("Request received for /childAlert with address : {}", address);
        List <ChildDTO> children = alertInfoService.getChildrenByAddress(address);

        if(children.isEmpty()){
            log.warn("No children found at this address {}:", address);
            return ResponseEntity.noContent().build();
        }
        log.info("{} child(ren) found at address {}", children.size(), address);
        return ResponseEntity.ok(children);
    }

    /**
     * Endpoint to retrieve a list of unique phone numbers of all persons covered by a specified fire station number.
     *
     * <p>Expects a query parameter "firestation" representing the station number.
     * Calls the service layer to get the phone numbers associated with that station.
     *
     * @param stationNumber the fire station number provided as a query parameter "firestation"
     * @return a ResponseEntity containing a list of unique phone numbers of persons covered by the fire station
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByStation(@RequestParam("firestation") int stationNumber) {

        List<String> phoneNumbers = alertInfoService.getPhoneNumbersByStation(stationNumber);
        log.info("Retrieving the telephone numbers of people covered by station number {}", stationNumber);
        log.debug("Number of phone numbers found: {}", phoneNumbers.size());
        return ResponseEntity.ok(phoneNumbers);

    }
}
