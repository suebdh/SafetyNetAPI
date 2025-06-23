package com.openclassrooms.safetynet.safetynetapi.service.mapper;

import com.openclassrooms.safetynet.safetynetapi.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.safetynetapi.model.MedicalRecord;
import org.springframework.stereotype.Service;

/**
 * Mapper service for converting between MedicalRecord domain objects and MedicalRecordDTO transfer objects.
 *
 * <p>This allows decoupling the internal data model from the API representation, facilitating data encapsulation and flexibility in response formats.</p>
 *
 * <ul>
 *     <li> toDTO(...) converts an entity to a DTO for use in controllers.</li>
 *     <li> toEntity(...) converts a DTO to an entity for persistence or business logic.</li>
 * </ul>
 *
 * @author Sarar
 */
@Service
public class MedicalRecordMapper {

    public MedicalRecordDTO toDTO(MedicalRecord medicalRecord) {
        return new MedicalRecordDTO(
                medicalRecord.getFirstName()
                , medicalRecord.getLastName()
                , medicalRecord.getBirthdate()
                , medicalRecord.getMedications()
                , medicalRecord.getAllergies()
        );
    }

    public MedicalRecord toEntity(MedicalRecordDTO medicalRecordDTO) {
        return new MedicalRecord(
                medicalRecordDTO.getFirstName(),
                medicalRecordDTO.getLastName(),
                medicalRecordDTO.getBirthdate(),
                medicalRecordDTO.getMedications(),
                medicalRecordDTO.getAllergies()
        );
    }
}
