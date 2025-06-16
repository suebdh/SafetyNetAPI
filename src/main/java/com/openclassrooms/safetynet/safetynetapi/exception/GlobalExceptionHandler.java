package com.openclassrooms.safetynet.safetynetapi.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global handler for application-wide exceptions thrown from REST controllers.
 * <p>
 * This class uses the annotation RestControllerAdvice to centralize exception handling,
 * and provides consistent HTTP responses and logs for expected exceptions like
 * PersonNotFoundException or PersonAlreadyExistsException
 * </p>
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where an attempt to retrieve, update, or delete a person fails
     * because the person does not exist in the system.
     *
     * @param ex the exception containing the error message indicating that the person was not found
     * @return a 404 Not Found HTTP response with the exception message as the response body
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<String> handlePersonNotFound(PersonNotFoundException ex) {
        log.warn("Can not do this operation, Person not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    /**
     * Handles cases where an attempt to create a person fails because the person already exists.
     *
     * @param ex the exception containing the error message indicating the person already exists
     * @return a 409 Conflict HTTP response with the exception message as the response body
     */
    @ExceptionHandler(PersonAlreadyExistsException.class)
    public ResponseEntity<String> handlePersonAlreadyExists(PersonAlreadyExistsException ex) {
        log.warn("Person already exists {} ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());

    }

    /**
     * Handles cases where an attempt to create a fire station fails because it already exists.
     *
     * @param ex the exception containing the error message indicating the fire station already exists
     * @return a 409 Conflict HTTP response with the exception message as the response body
     */
    @ExceptionHandler(FireStationAlreadyExistsException.class)
    public ResponseEntity<String> handleFireStationAlreadyExists(FireStationAlreadyExistsException ex) {
        log.warn("FireStation already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles cases where an attempt to retrieve, update, or delete a fire station fails
     * because the fire station does not exist in the system.
     *
     * @param ex the exception containing the error message indicating that the fire station was not found
     * @return a 404 Not Found HTTP response with the exception message as the response body
     */
    @ExceptionHandler(FireStationNotFoundException.class)
    public ResponseEntity<String> handleFireStationNotFound(FireStationNotFoundException ex) {
        log.warn("Can not do this operation, FireStation not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

    }


    }
