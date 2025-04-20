//package org.example.ibb_ecodation_javafx.core.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//public class PropertyConfig {
//
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        configurer.setLocations(
//                new ClassPathResource("application.properties"),
//                new ClassPathResource("application-mssql.properties")
//        );
//        configurer.setIgnoreUnresolvablePlaceholders(false);
//        configurer.setIgnoreResourceNotFound(false);
//        System.out.println("PropertySourcesPlaceholderConfigurer initialized.");
//        return configurer;
//    }
//}
