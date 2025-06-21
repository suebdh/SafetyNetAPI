package com.openclassrooms.safetynet.safetynetapi.service;

import com.openclassrooms.safetynet.safetynetapi.dto.*;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationAlreadyExistsException;
import com.openclassrooms.safetynet.safetynetapi.exception.FireStationNotFoundException;
import com.openclassrooms.safetynet.safetynetapi.model.FireStation;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import com.openclassrooms.safetynet.safetynetapi.model.Person;
import com.openclassrooms.safetynet.safetynetapi.repository.FireStationRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.safetynetapi.repository.PersonRepository;
import com.openclassrooms.safetynet.safetynetapi.util.AgeUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing fire station data and operations.
 * <p>
 * This service interacts with the FireStationRepository to perform CRUD operations
 * and contains business logic related to fire stations, such as validation
 * and exception handling for non-existing or duplicate fire stations.
 * </p>
 */
@Log4j2
@Service
@Data
public class FireStationService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    /**
     * Retrieves all fire stations from the repository.
     *
     * @return a list of all FireStation objects; the list may be empty if no fire stations are found.
     */
    public List<FireStation> getAllFireStations() {
        List<FireStation> fireStations = fireStationRepository.getFireStations();
        log.info("{} fire station(s) found", fireStations.size());
        return fireStations;
    }

    /**
     * Retrieves a fire station by its address.
     * <p>
     * This method searches for a fire station matching the provided address.
     * If a fire station is found, it is returned; otherwise, a
     * {@link FireStationNotFoundException} is thrown.
     * </p>
     *
     * @param address the address of the fire station to retrieve
     * @return the FireStation found at the specified address
     * @throws FireStationNotFoundException if no fire station is found at the given address
     */
    public FireStation getFireStationByAddress(String address) {
        log.info("Request received to find firestation by address: '{}'", address);

        FireStation fireStation = fireStationRepository.getFireStationByAddress(address);

        if (fireStation == null) {
            log.error("No firestation found at address '{}'", address);
            throw new FireStationNotFoundException("No firestation found at address: " + address);
        }

        log.info("FireStation found at address '{}'", address);
        return fireStation;
    }

    /**
     * Updates an existing fire station's information.
     * <p>
     * This method attempts to update the fire station associated with the given address.
     * If no matching fire station is found, a FireStationNotFoundException is thrown.
     *
     * @param fireStation the FireStation object containing updated address and station number
     * @return the updated FireStation object
     * @throws FireStationNotFoundException if no fire station is found at the specified address
     */
    public FireStation updateFireStation(FireStation fireStation) {
        log.info("Request received to update firestation at address '{}'", fireStation.getAddress());

        FireStation fireStationToUpdate = fireStationRepository.updateFireStation(fireStation);

        if (fireStationToUpdate == null) {
            log.error("No firestation found at address '{}', cannot update", fireStation.getAddress());
            throw new FireStationNotFoundException("No firestation found at address: " + fireStation.getAddress());
        }

        log.info("FireStation at address '{}' updated to station number {}", fireStationToUpdate.getAddress(), fireStationToUpdate.getStation());
        return fireStationToUpdate;
    }

    /**
     * Saves a new fire station to the system.
     * <p>
     * This method stores the provided FireStation object if no fire station already exists at the specified address.
     * If a fire station is already present, a FireStationAlreadyExistsException is thrown.
     *
     * @param fireStation the FireStation object to be saved
     * @return the saved FireStation object
     * @throws FireStationAlreadyExistsException if a fire station already exists at the given address
     */
    public FireStation saveFireStation(FireStation fireStation) {
        log.info("Request received to save firestation at address '{}' with station number {}",
                fireStation.getAddress(), fireStation.getStation());

        FireStation existing = fireStationRepository.getFireStationByAddress(fireStation.getAddress());

        if (existing != null) {
            log.error("FireStation already exists at address '{}'", fireStation.getAddress());
            throw new FireStationAlreadyExistsException("FireStation already exists at address: " + fireStation.getAddress());
        }

        fireStationRepository.saveFireStation(fireStation);

        log.info("FireStation saved successfully at address '{}' with station number {}",
                fireStation.getAddress(), fireStation.getStation());

        return fireStation;
    }

    /**
     * Deletes a fire station identified by its address.
     * <p>
     * This method attempts to delete the first occurrence of a fire station at the specified address.
     * If no fire station is found at the given address, a FireStationNotFoundException is thrown.
     *
     * @param address the address of the fire station to delete
     * @throws FireStationNotFoundException if no fire station is found at the specified address
     */
    public void deleteFireStationByAddress(String address) {
        log.info("Request received to delete firestation at address '{}'", address);

        boolean deleted = fireStationRepository.deleteFirstOccurrenceFireStationByAddress(address);

        if (deleted) {
            log.info("FireStation at address '{}' successfully deleted", address);
        } else {
            log.error("No firestation found at address '{}', cannot delete", address);
            throw new FireStationNotFoundException("No firestation found at address: " + address);
        }
    }

    /**
     * Deletes all fire stations associated with the given station number.
     * <p>
     * If no fire stations are found with the specified station number, a FireStationNotFoundException is thrown.
     *
     * @param stationNumber the station number whose associated fire stations should be deleted
     * @throws FireStationNotFoundException if no fire stations are found with the given station number
     */
    public void deleteFireStationsByStationNumber(int stationNumber) {
        log.info("Request received to delete all firestations with station number {}", stationNumber);

        boolean deleted = fireStationRepository.deleteByStationNumber(stationNumber);

        if (deleted) {
            log.info("All firestations with station number {} successfully deleted", stationNumber);
        } else {
            log.error("No firestations found with station number {}, nothing deleted", stationNumber);
            throw new FireStationNotFoundException("No firestations found with station number: " + stationNumber);
        }
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
