# SafetyNetAPI

Backend API for SafetyNet Alerts application, developed as part of the OpenClassrooms Java developer curriculum.

## Table of Contents
- [Description](#description)
- [Tech Stack](#tech-stack)
- [Usage](#usage)
- [Testing](#testing)
- [Base URL](#base-url)
- [API Endpoints](#api-endpoints)
    - [Person endpoints](#person-endpoints)
    - [FireStation endpoints](#firestation-endpoints)
    - [MedicalRecord endpoints](#medicalrecord-endpoints)
    - [Specific Alert and Information Requests](#specific-alert-and-information-requests)

## Description

**SafetyNetAPI** is a REST API implemented with Spring Boot, designed to provide emergency services with relevant information about individuals in distress and their locations.  
This application reads and stores data in a JSON file and exposes endpoints to retrieve and update this information.  
Its primary goal is to send crucial data to emergency service systems efficiently.

## Tech Stack

- Java 21
- Spring Boot
- Maven
- Git for version control
- Jackson : Java library for JSON parsing
- Integration tests with Spring Test (`@SpringBootTest`, `@AutoConfigureMockMvc`, `MockMvc` with `@Autowired`)
- Unit testing with JUnit
- Code coverage measured with JaCoCo
- Logging (execution traces) with Log4j2

## Usage

- Clone the repository
- Build with Maven: `mvn clean install`
- Run the application: `mvn spring-boot:run`

## Testing

- Run unit and integration tests:  
  `mvn test`

- Run full verification phase (includes tests, checks, and coverage):  
  `mvn verify`

- Generate an HTML test report (Surefire):  
  `mvn surefire-report:report`  
  → Open `target/reports/surefire.html` in a browser

- View code coverage report (JaCoCo):  
  → Open `target/site/jacoco/index.html` in a browser

## Base URL
`http://localhost:8080`

## API Endpoints

**Note:**
- For `/person` and `/medicalrecord`, the unique identifier is the combination of `firstName` and `lastName`.
- For `/firestation`, the unique identifier is the `address`.

---

### Person endpoints

#### /persons
- `GET /persons`  
  Retrieves a list of all persons in the system, including their details.

#### /person
- `GET /person?firstName=<firstName>&lastName=<lastName>`  
  Retrieves a single person identified by `firstName` and `lastName`.

- `POST /person`  
  Adds a new person. Requires all necessary fields such as `firstName`, `lastName`, `address`, `email`, `phone`, etc.

- `PUT /person`  
  Updates an existing person. The person to update is identified by the `firstName` and `lastName` fields in the request body. Other fields can be modified.

- `DELETE /person?firstName=<firstName>&lastName=<lastName>`  
  Deletes the person identified by `firstName` and `lastName` provided as query parameters.

---

### FireStation endpoints

#### /firestations
- `GET /firestations`  
  Retrieves a list of all fire stations.

- `DELETE /firestations?stationNumber=<stationNumber>`  
  Deletes all fire stations associated with the given `stationNumber`.

#### /firestation
- `POST /firestation`  
  Adds a new fire station mapping. Requires fields: `address` and `station` in the request body.

- `PUT /firestation`  
  Updates an existing fire station mapping. Requires the full fire station object (including `address`) in the request body.

- `DELETE /firestation?address=<address>`  
  Deletes the fire station mapping for the specified `address`.

---

### MedicalRecord endpoints

#### /medicalrecords
- `GET /medicalrecords`  
  Retrieves a list of all medical records.

#### /medicalrecord
- `GET /medicalrecord?firstName=<firstName>&lastName=<lastName>`  
  Retrieves the medical record for the specified `firstName` and `lastName`.

- `POST /medicalrecord`  
  Adds a new medical record. Requires fields such as `firstName`, `lastName`, `birthdate`, `medications`, `allergies`, etc. in the request body.

- `PUT /medicalrecord`  
  Updates an existing medical record. Requires the full medical record DTO in the request body.

- `DELETE /medicalrecord?firstName=<firstName>&lastName=<lastName>`  
  Deletes the medical record identified by `firstName` and `lastName`.

---

### Specific Alert and Information Requests

These endpoints provide specific queries to retrieve alert-related or aggregated information useful for emergency services.

| Method | URL               | Parameters                     | Description & Details                                                                                        |
|--------|-------------------|--------------------------------|--------------------------------------------------------------------------------------------------------------|
| GET    | `/communityEmail` | `city`                         | Retrieves all email addresses of persons living in the specified city. Returns 204 No Content if none found. |
| GET    | `/personInfo`     | `lastName`                     | Retrieves detailed personal info for persons matching the last name. Returns 404 if no matches.              |
| GET    | `/childAlert`     | `address`                      | Lists children and household members living at the given address. Always returns 200 OK, even if empty.      |
| GET    | `/phoneAlert`     | `firestation` (station number) | Returns unique phone numbers of persons covered by the fire station.                                         |
| GET    | `/firestation`    | `stationNumber`                | Retrieves persons covered by the fire station, including counts of adults and children.                      |
| GET    | `/fire`           | `address`                      | Provides residents at the address with age, phone, medications, allergies, and the fire station number.      |
| GET    | `/flood/stations` | `stations` (list of numbers)   | Returns households served by given fire stations, including personal and medical details of residents.       |