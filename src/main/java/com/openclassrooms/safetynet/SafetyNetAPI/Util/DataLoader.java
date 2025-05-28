package com.openclassrooms.safetynet.SafetyNetAPI.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.SafetyNetAPI.Model.DataFile;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class DataLoader {

    @Autowired
    private ObjectMapper objectMapper;

    @Getter
    private DataFile dataFile;

    @PostConstruct
    public void init() {
        String fileName ="data.json";
        log.info("Start reading file {}", fileName);
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            if (is==null)  throw new RuntimeException("The "+ fileName + " is not found !");
            dataFile = objectMapper.readValue(is, DataFile.class);
            log.info("Successfully transformed JSON file '{}' into Java object!", fileName);
        } catch (IOException e) {
            log.error("Error loading JSON '{}': {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Error loading json file"+ e.getMessage(), e);
        }
    }
}
