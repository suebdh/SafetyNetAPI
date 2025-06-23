package com.openclassrooms.safetynet.safetynetapi.controller;

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
}
