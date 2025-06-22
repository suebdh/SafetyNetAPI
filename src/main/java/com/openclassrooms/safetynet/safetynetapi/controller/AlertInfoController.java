package com.openclassrooms.safetynet.safetynetapi.controller;

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

}
