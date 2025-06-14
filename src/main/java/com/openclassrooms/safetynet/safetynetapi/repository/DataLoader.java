package com.openclassrooms.safetynet.safetynetapi.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.safetynetapi.model.DataFile;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Spring component responsible for loading and saving application data from/to a JSON file.
 *
 * <p>This class attempts to load data from an external JSON file specified by the configurable path
 * (e.g., "./data/data.json").
 * If the external file does not exist, it loads the default data from the resources folder
 * ("data.json" in src/main/resources) and creates the external file with this default content.</p>
 *
 * <p>This class provides access to the loaded data via a getter method, and supports saving any changes back to
 * the external JSON file.</p>
 */

@Log4j2
@Component
public class DataLoader {

    private final ObjectMapper objectMapper;
    private final String dataFilePath;

    @Getter
    private DataFile dataFile;

    /**
     * Constructs a DataLoader with the given ObjectMapper and data file path.
     *
     * @param objectMapper the ObjectMapper used for JSON serialization and deserialization
     * @param dataFilePath the path to the external JSON data file (injected from application properties)
     */
    public DataLoader(ObjectMapper objectMapper,
                      @Value("${data.file.path}") String dataFilePath) {
        this.objectMapper = objectMapper;
        this.dataFilePath = dataFilePath;
    }

    /**
     * Loads the application data from a JSON file after the bean's construction.
     *
     * <p>If an external JSON file exists at the configured path, it loads data from this file.
     * Otherwise, it loads the default data from the bundled resource file "data.json" and
     * creates the external file with this default content for future use.</p>
     *
     * <p>Throws a RuntimeException if the file cannot be read or if the default resource file is missing.</p>
     */
    @PostConstruct
    public void loadJsonFile() {
        File file = new File(dataFilePath);

        if (file.exists()) {
            log.info("Loading data from external file '{}'", dataFilePath);
            try {
                dataFile = objectMapper.readValue(file, DataFile.class);
                log.info("Successfully loaded data from '{}'", dataFilePath);
            } catch (IOException e) {
                log.error("Failed to read external file '{}'", dataFilePath, e);
                throw new RuntimeException("Cannot read data file", e);
            }
        } else {
            log.warn("External file '{}' not found. Loading default from resources.", dataFilePath);
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
                if (is == null) throw new RuntimeException("Default 'data.json' not found in resources!");
                dataFile = objectMapper.readValue(is, DataFile.class);
                log.info("Loaded default data from resources. Saving to '{}'", dataFilePath);
                saveJsonFile(); // Create the external file on first run
            } catch (IOException e) {
                log.error("Error loading fallback JSON", e);
                throw new RuntimeException("Error loading fallback JSON", e);
            }
        }
    }

    /**
     * Saves the current data stored in dataFile to the external JSON file specified by dataFilePath.
     *
     * <p>This method uses the Jackson (ObjectMapper) to serialize the data to a pretty-printed JSON file.</p>
     *
     * @throws RuntimeException if an I/O error occurs while writing to the file.
     */
    public void saveJsonFile() {
        File file = new File(dataFilePath);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, dataFile);
            log.info("Successfully saved data to '{}'", dataFilePath);
        } catch (IOException e) {
            log.error("Error saving JSON to '{}': {}", dataFilePath, e.getMessage(), e);
            throw new RuntimeException("Error saving json file: " + e.getMessage(), e);
        }
    }
}
