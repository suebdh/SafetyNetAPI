package com.openclassrooms.safetynet.safetynetapi.repository;

import com.openclassrooms.safetynet.safetynetapi.SafetyNetApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SafetyNetApiApplication.class)
public class DataLoaderTest {

    @Autowired
    public DataLoader dataLoader;

    @Test
    public void testDataFileIsLoaded(){
        assertNotNull(dataLoader.getDataFile(), "dataFile should not be null!");
        assertThat(dataLoader.getDataFile().getPersons()).isNotEmpty();

        String firstName = dataLoader.getDataFile().getPersons().get(0).getFirstName();
        assertThat(firstName).isEqualTo("John");
    }
}
