package org.example.ibb_ecodation_javafx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.config.DatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class YamlReader {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static DatabaseConfig readDatabaseConfig(String yamlFilePath) throws IOException {
        File file = new File(YamlReader.class.getResource(yamlFilePath).getFile());
            return mapper.readValue(file, DatabaseConfig.class);

    }
}