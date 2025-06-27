package com.openclassrooms.safetynet.safetynetapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
@AllArgsConstructor
public class FireStation {
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private int station;
}
