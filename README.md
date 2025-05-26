# SafetyNetAPI

Backend API for SafetyNet Alerts application, developed as part of the OpenClassrooms Java developer curriculum.

## Table of Contents
- [Description](#description)
- [Tech Stack](#tech-stack)
- [Usage](#usage)
- [Base URL](#base-url)
- [API Endpoints](#api-endpoints)
    - [/persons](#persons)
    - [/firestations](#firestations)
    - [/medicalrecords](#medicalrecords)
    - [/addresses](#addresses)
## Description

**SafetyNetAPI** is a REST API implemented with Spring Boot, designed to provide emergency services with relevant information about individuals in distress and their locations.
This application reads and stores data in a JSON file and exposes endpoints to retrieve and update this information.
Its primary goal is to send crucial data to emergency service systems efficiently.

## Tech Stack

- Java 11+
- Spring Boot
- Maven (preferred, but Gradle is also possible)
- Git for version control
- Java library for JSON parsing (e.g., Jackson)
- Unit testing with JUnit
- Code coverage measured with JaCoCo
- Logging (execution traces) with Log4j

## Usage

- Clone the repository
- Build with Maven: `mvn clean install`
- Run the application


## Base URL
http://localhost:8080

## API Endpoints

### /persons
- `GET /persons?lastName=<lastName>`
  Returns the name, address, age, email, and medical records for each person with the specified last name. Multiple persons with the same last name will all be listed.
- `GET /emails?city=<city>`
  Returns the email addresses of all residents in the specified city.
  *Replace `<city>` with the city name. If the city name contains spaces, encode them as `%20` or use `+`. For example, `New%20York` or `New+York`.*
- `POST /persons`
   Adds a new person
- `PUT /persons/{firstName}-{lastName}`
   Updates an existing person (assumes first and last names do not change, other fields can)
- `DELETE /persons/{firstName}-{lastName}`  
   Deletes a person (unique identifier is first name + last name)

### /firestations
- `GET /firestations/{firestationId}/residents`
  Returns a list of people covered by the specified fire station number. The response includes first name, last name, address, phone number, and a count of adults and children (18 years or younger) in the area.
  *Replace `{firestationId}` with the fire station number (e.g., 1, 2, 3)*
- `GET /firestations/{firestationId}/phones`
  Returns a list of phone numbers of residents covered by the specified fire station, used for sending emergency text messages.
- `GET /stations/flood?ids=1,2,3`
  Returns a list of households served by the specified fire stations, grouped by address. For each person, includes name, phone number, age, and medical records.
  *Query parameter `ids` with a comma-separated list of fire station numbers*
- `POST /firestations` 
   Adds a new address-to-firestation mapping. The request body should include the address and fire station number.
- `PUT /firestations/{address}` 
   Updates the fire station number for the specified address.
- `DELETE /firestations/{address}`
   Deletes the mapping for the specified address.

### /medicalrecords
- `POST /medicalrecords`
   Adds a new medical record. The request body must include all necessary details along with the person's first and last name.
- `PUT /medicalrecords/{firstName}-{lastName}`
   Updates an existing medical record identified by first and last name.
- `DELETE /medicalrecords/{firstName}-{lastName}`
   Deletes the medical record identified by first and last name.

### /addresses
- `GET /addresses/{address}/children`
  Returns a list of children (18 years or younger) living at the given address, including their first name, last name, age, and a list of other household members. Returns an empty string if no children are found.
- `GET /addresses/{address}/residents`
  Returns a list of residents at the given address along with the fire station number serving that address. Includes name, phone number, age, and medical records (medications, dosages, allergies).



