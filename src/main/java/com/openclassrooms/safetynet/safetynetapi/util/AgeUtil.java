package com.openclassrooms.safetynet.safetynetapi.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeUtil {
    /**
     * Calculates the age in years based on the given birth date.
     *
     * @param birthDate the birth date
     * @return the calculated age in years
     */
    public static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
