//package org.example.ibb_ecodation_javafx.config;
//
//import org.example.ibb_ecodation_javafx.core.config.DatabaseConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.FileSystemResource;
//
//@Configuration
//@ComponentScan(basePackages = "org.example.ibb_ecodation_javafx")
//public class ApplicationConfig {
//
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//
//        String activeProfile = System.getProperty("spring.profiles.active", "mssql");
//        String propertiesFile = String.format("./src/main/resources/db/%s.properties", activeProfile);
//
//        configurer.setLocation(new FileSystemResource(propertiesFile));
//        return configurer;
//    }
//
//    @Bean
//    public DatabaseConfig databaseConfig() {
//        return new DatabaseConfig();
//    }
//}
