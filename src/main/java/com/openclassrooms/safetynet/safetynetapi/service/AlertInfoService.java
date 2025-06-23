package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.ChildDTO;
import com.openclassrooms.safetynet.safetynetapi.dto.HouseholdMembersDTO;
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
import java.util.Collections;
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





    /**
     * Retrieves a list of children (aged 18 or under) living at the specified address,
     * along with their household members.
     *
     * <p>For each person found at the address, the method attempts to retrieve their medical record
     * to determine their age. If the person is 18 years old or younger, they are considered a child.
     * The result includes their first name, last name, age, and a list of other household members
     * (excluding the child himself).</p>
     *
     * @param address the address to search for children
     * @return a list of ChildDTO objects representing each child and their household members.
     * *         Returns an empty list if no residents are found at the address or if no children live there.
     */
    public List<ChildDTO> getChildrenByAddress(String address) {

        // 1- Retrieve all persons living at the given address
        List<Person> personsAtAddress = personRepository.getPersonByAddress(address);

        if (personsAtAddress.isEmpty()) {
            log.warn("No residents found at address {}", address);
            return Collections.emptyList();
        }
        // 2- For each person:
        //    -- calculate their age using their medical record
        //    -- if age ≤ 18 → they are considered a child
        //    -- add other household members (excluding the child) to the DTO
        List<ChildDTO> children = new ArrayList<>();
        for (Person person : personsAtAddress) {
            MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (record == null) {
                log.warn("No medical record found for {} {}", person.getFirstName(), person.getLastName());
                continue;
            } // No medical record → skip
            int age = AgeUtil.calculateAge(record.getBirthdate());
            if (age <= 18) {
                ChildDTO child = new ChildDTO();
                child.setFirstName(person.getFirstName());
                child.setLastName(person.getLastName());
                child.setAge(age);

                // Household members excluding the child
                List<HouseholdMembersDTO> otherMembers = personsAtAddress.stream()
                        .filter(p -> !(p.getFirstName().equalsIgnoreCase(person.getFirstName())
                                && p.getLastName().equalsIgnoreCase(person.getLastName())))
                        .map(p -> {
                            HouseholdMembersDTO dto = new HouseholdMembersDTO();
                            dto.setFirstName(p.getFirstName());
                            dto.setLastName(p.getLastName());
                            return dto;
                        })
                        .collect(Collectors.toList());

                child.setHouseholdMembers(otherMembers);

                children.add(child);
            }
        }
        // 3- Return the list of ChildDTO
        return children;
    }
}
