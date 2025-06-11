package com.openclassrooms.safetynet.safetynetapi.exception;

public class FireStationAlreadyExistsException extends RuntimeException {
    public FireStationAlreadyExistsException(String message) {
        super(message);
    }
}
