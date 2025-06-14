package com.openclassrooms.safetynet.safetynetapi.service.mapper;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonDTO;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonMapper {

    public PersonDTO toDTO(Person person) {
        return new PersonDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail()
        );
    }

    public Person toEntity(PersonDTO persondto) {
        Person person = new Person();
        person.setFirstName(persondto.getFirstName());
        person.setLastName(persondto.getLastName());
        person.setAddress(persondto.getAddress());
        person.setCity(persondto.getCity());
        person.setZip(persondto.getZip());
        person.setPhone(persondto.getPhone());
        person.setEmail(persondto.getEmail());
        return person;
    }


}
