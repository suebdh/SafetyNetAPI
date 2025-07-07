package com.openclassrooms.safetynet.safetynetapi;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class SafetyNetApiApplication {

	public static void main(String[] args) {

		log.info("Starting SafetyNet Application...");
		SpringApplication.run(SafetyNetApiApplication.class, args);
		log.info("SafetyNetApi application started successfully.");
	}

}
