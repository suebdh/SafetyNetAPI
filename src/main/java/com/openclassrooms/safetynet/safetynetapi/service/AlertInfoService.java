package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.PersonInfoDto;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.AlertInfoInterfaceRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.util.AgeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AlertInfoService {

    @Autowired
    private AlertInfoInterfaceRepository alertInfoInterfaceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Retrieves a list of unique email addresses of all persons living in the specified city.
     * <p>
     * This method filters persons by city, extracts their emails, and removes duplicates.
     * </p>
     *
     * @param city the name of the city for which to retrieve email addresses
     * @return a list of distinct email addresses of residents in the given city
     */
    public List<String> getEmailsByCity(String city) {
        List<Person> persons = alertInfoInterfaceRepository.findByCity(city);
        return persons.stream().map(Person::getEmail).distinct().collect(Collectors.toList());
    }

    /**
     * Retrieves a list of PersonInfoDto objects for all persons matching the given last name.
     * <p>
     * For each person found, this method fetches their medical record to include
     * age (calculated from birthdate), medications, and allergies.
     *
     * @param lastName the last name to search for
     * @return a list of PersonInfoDto containing personal and medical information
     */
    public List<PersonInfoDto> getPersonInfoByLastName(String lastName) {
        List<Person> persons = personRepository.findByLastName(lastName);
        List<PersonInfoDto> result = new ArrayList<>();

        for (Person person : persons) {
            MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                    person.getFirstName(), person.getLastName());

            if (record != null) {
                int age = AgeUtil.calculateAge(record.getBirthdate());
                PersonInfoDto dto = new PersonInfoDto(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getEmail(),
                        age,
                        record.getMedications(),
                        record.getAllergies()
                );
                result.add(dto);
            }
        }

        return result;
    }

}
