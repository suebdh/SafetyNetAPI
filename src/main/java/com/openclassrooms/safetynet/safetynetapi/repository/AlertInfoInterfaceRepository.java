package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertInfoInterfaceRepository {
    List<Person> findByCity(String city);
}
