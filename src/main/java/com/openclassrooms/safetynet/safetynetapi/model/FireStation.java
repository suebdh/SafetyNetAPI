package com.openclassrooms.safetynet.safetynetapi.model;

import lombok.Data;

/**
 * Represents a firestation mapping an address to a station number.
 *
 * <p>This class contains the address of a firestation and its corresponding station number.</p>
 *
 * <p>Uses Lombok's @Data annotation to automatically generate getters, setters,
 * equals, hashCode, and toString methods.</p>
 *
 * @author [Sarar]
 */
@Data
public class FireStation {
    private String address;
    private int station;
}
