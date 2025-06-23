package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.*;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.AlertInfoInterfaceRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.util.AgeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private FireStationRepository fireStationRepository;

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

    /**
     * Retrieves a list of unique phone numbers for all persons covered by a given fire station number.
     *
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Fetches all fire stations associated with the provided station number.</li>
     *   <li>Extracts the addresses covered by these fire stations.</li>
     *   <li>Retrieves all persons living at those addresses.</li>
     *   <li>Extracts and returns a list of distinct phone numbers from these persons.</li>
     * </ol>
     *
     * @param stationNumber the fire station number to search for
     * @return a list of unique phone numbers of persons covered by the fire station
     * @throws FireStationNotFoundException if no fire stations are found for the given station number
     */
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // 1. Retrieve all fire stations with this station number
        List<FireStation> fireStations = fireStationRepository.getFireStationByStationNumber(stationNumber);
        if (fireStations.isEmpty()) {
            throw new FireStationNotFoundException("Fire station(s) with station number" + stationNumber + " are not found.");
        }

        // 2. Extract the addresses of these fire stations
        List<String> addresses = fireStations.stream()
                .map(FireStation::getAddress)
                .toList();

        log.info("Station number {} covers {} addresses", stationNumber, addresses.size());

        // 3. Retrieve all persons living at these addresses
        List<Person> personsCovered = new ArrayList<>();
        for (String address : addresses) {
            personsCovered.addAll(personRepository.getPersonByAddress(address));
        }
        log.debug("Found {} persons covered by this station", personsCovered.size());


        // 4. Extract unique phone numbers
        List<String> phoneNumbers = personsCovered.stream()
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());

        log.debug("Returning {} unique phone numbers", phoneNumbers.size());
        return phoneNumbers;
    }


    /**
     * Retrieves information about all persons covered by a given fire station number.
     * <p>
     * This includes:
     * <ul>
     *   <li>All persons living at addresses associated with the fire station</li>
     *   <li>Counts of adults (age > 18) and children (age ≤ 18)</li>
     *   <li>A list of basic person information (first name, last name, address, phone)</li>
     * </ul>
     *
     * @param stationNumber the fire station number used to retrieve covered addresses
     * @return a CoveredPersonsByStationDTO containing the list of persons, the number of adults,
     * and the number of children
     * @throws FireStationNotFoundException if no addresses are found for the given station number
     */
    public CoveredPersonsByStationDTO getPersonsCoveredByStation(int stationNumber) {
        // 1- Find all addresses covered by the given fire station
        List<FireStation> fireStations = fireStationRepository.getFireStationByStationNumber(stationNumber);
        List<String> addresses = fireStations.stream()
                .map(FireStation::getAddress)
                .distinct()
                .toList();

        if (addresses.isEmpty()) {
            log.warn("No addresses found for station number: {}", stationNumber);
            throw new FireStationNotFoundException("No addresses found for station number: " + stationNumber);
        }

        // 2- Retrieve all persons living at these addresses
        List<Person> personsCovered = new ArrayList<>();
        for (String address : addresses) {
            personsCovered.addAll(personRepository.getPersonByAddress(address));
        }
        log.debug("Found {} persons covered by station number {}", personsCovered.size(), stationNumber);

        // 3- Count adults and children, and build DTO list
        int nbChildren = 0;
        int nbAdults = 0;
        List<CoveredPersonsDTO> dtoList = new ArrayList<>();

        for (Person person : personsCovered) {
            MedicalRecord record = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());

            if (record == null) {
                log.warn("No medical record found for {} {}", person.getFirstName(), person.getLastName());
                continue; // Skip person if no medical record
            }
            int age = AgeUtil.calculateAge(record.getBirthdate());
            if (age <= 18) {
                nbChildren++;
                log.debug("Incrementing the number of children");
            } else {
                nbAdults++;
                log.debug("Incrementing the number of adults");
            }

            CoveredPersonsDTO dto = new CoveredPersonsDTO();
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setAddress(person.getAddress());
            dto.setPhone(person.getPhone());
            dtoList.add(dto);
        }

        // 4- Build and return the final response DTO

        CoveredPersonsByStationDTO responseDTO = new CoveredPersonsByStationDTO();
        responseDTO.setCoveredPersons(dtoList);
        responseDTO.setNbAdults(nbAdults);
        responseDTO.setNbChildren(nbChildren);
        return responseDTO;
    }

    private FirePersonInfoDTO buildFirePersonInfoDTO(Person person){
        MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());

        int age = -1;
        List<String> medications = Collections.emptyList();
        List<String> allergies = Collections.emptyList();

        if (medicalRecord != null) {
            age = AgeUtil.calculateAge(medicalRecord.getBirthdate());
            medications = medicalRecord.getMedications();
            allergies = medicalRecord.getAllergies();
        } else {
            log.warn("No medical record found for1 {} {}", person.getFirstName(), person.getLastName());
        }

        FirePersonInfoDTO infoDto = new FirePersonInfoDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getPhone(),
                age,
                medications,
                allergies
        );
        return infoDto;
    }

    /**
     * Retrieves detailed information about residents living at a specific address,
     * including their personal details and medical records, as well as the fire station number covering that address.
     *
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Finds the fire station assigned to the given address.</li>
     *   <li>Fetches all persons residing at that address.</li>
     *   <li>Retrieves each person's age, medications, and allergies using their medical records.</li>
     *   <li>Builds and returns a FireStationResidentsDTO containing the station number and a list of detailed resident info.</li>
     * </ul>
     *
     * @param address the address for which to retrieve residents and fire station information
     * @return a FireStationResidentsDTO containing the fire station number and a list of residents with their medical details
     * @throws FireStationNotFoundException if no fire station is found for the provided address
     */
    public FireStationResidentsDTO getResidentsByAddress(String address) {

        //1 - Find the fire station using the address
        FireStation fireStation = fireStationRepository.getFireStationByAddress(address);
        if (fireStation == null) {
            log.warn("No fire station found for address {}", address);
            throw new FireStationNotFoundException("No fire station found for address: " + address);
        }

        int stationNumber = fireStation.getStation();

        //2 - Find the residents at the address
        List<Person> residents = personRepository.getPersonByAddress(address);
        log.info("{} resident(s) found at address {}", residents.size(), address);

        if (residents.isEmpty()) {
            log.warn("No residents found at address {}", address);
            // Option 1: Return a DTO with an empty list
            return new FireStationResidentsDTO(stationNumber, Collections.emptyList());
            // Option 2:  throw new PersonNotFoundException("No residents found at address: " + address);
        }

        //3 - Build the detailed list of residents
        List<FirePersonInfoDTO> detailedResidents = new ArrayList<>();
        for (Person person : residents) {
            FirePersonInfoDTO infoDto = buildFirePersonInfoDTO(person);
            detailedResidents.add(infoDto);
        }
/*
    List<FirePersonInfoDTO> detailedResidents = residents.stream()
            .map(this::buildFirePersonInfoDTO)
            .collect(Collectors.toList());
        */
        //4 - Return le DTO final
        return new FireStationResidentsDTO(stationNumber, detailedResidents);
    }

    /**
     * Retrieves a list of households (grouped by address) served by the given fire station numbers.
     *
     * <p>For each fire station number provided, this method fetches the addresses it covers,
     * then gathers all residents living at those addresses along with their personal and medical information
     * (first name, last name, phone, age, medications, and allergies).</p>
     *
     * <p>This method is used for the /flood/stations endpoint to support flood-related alerts and preparedness.</p>
     *
     * @param stationNumbers the list of fire station numbers to retrieve households for
     * @return a list of AddressResidentsDTO, each containing an address and the detailed info of its residents
     */
    public List<AddressResidentsDTO> getHouseholdsByStations(List<Integer> stationNumbers) {
        // 1. Retrieve all addresses covered by the requested stations
        Set<String> addresses = new HashSet<>(); //Set to eliminate duplicated addresses
        for (Integer stationNumber : stationNumbers) {
            List<String> addressesForStation = fireStationRepository.getAddressesByStation(stationNumber);
            addresses.addAll(addressesForStation);
        }

        // 2. Initialize the result list
        List<AddressResidentsDTO> result = new ArrayList<>();

        //3. For each address, build the residents with their medical info and add them to the resul

        for (String address : addresses) {
            List<Person> residents = personRepository.getPersonByAddress(address);

            List<FirePersonInfoDTO> residentDtos = new ArrayList<>();
            for (Person person : residents) {
                FirePersonInfoDTO dto = buildFirePersonInfoDTO(person);
                residentDtos.add(dto);
            }

            AddressResidentsDTO addressResidentsDTO = new AddressResidentsDTO(address, residentDtos);
            result.add(addressResidentsDTO);
        }

        // 4. Return the complete list
        return result;
    }
}
