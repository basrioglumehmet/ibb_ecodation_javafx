package org.example.ibb_ecodation_javafx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;
import org.example.ibb_ecodation_javafx.properties.DatabaseProperty;
import org.example.ibb_ecodation_javafx.properties.SendGridProperty;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class YamlReader {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static DatabaseProperty readDatabaseConfig(String yamlFilePath) throws IOException {
        File file = new File(YamlReader.class.getResource(yamlFilePath).getFile());
            return mapper.readValue(file, DatabaseProperty.class);

    }

    public static SendGridProperty readSendGridProperty(String yamlFilePath) throws IOException {
        File file = new File(YamlReader.class.getResource(yamlFilePath).getFile());
        return mapper.readValue(file, SendGridProperty.class);

    }
}