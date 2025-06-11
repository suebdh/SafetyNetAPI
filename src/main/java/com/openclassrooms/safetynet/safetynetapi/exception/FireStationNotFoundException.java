package com.openclassrooms.safetynet.safetynetapi.exception;

public class FireStationNotFoundException extends RuntimeException {
    public FireStationNotFoundException(String message) {
        super(message);
    }
}
