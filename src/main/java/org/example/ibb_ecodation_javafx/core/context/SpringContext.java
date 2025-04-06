package org.example.ibb_ecodation_javafx.core.context;


import lombok.experimental.UtilityClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@UtilityClass
public class SpringContext {

    private static ClassPathXmlApplicationContext context;

    // Static initializer for global context
    static {
        context = new ClassPathXmlApplicationContext("spring-annotation.xml");
    }

    // Method to get the global Spring context
    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

    // Method to close the Spring context
    public static void closeContext() {
        if (context != null) {
            context.close();
        }
    }
}