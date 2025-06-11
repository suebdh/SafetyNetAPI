package com.openclassrooms.safetynet.safetynetapi.exception;

public class MedicalRecordAlreadyExistsException extends RuntimeException {
    public MedicalRecordAlreadyExistsException(String message) {
        super(message);
    }
}
