package org.example.ibb_ecodation_javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.ibb_ecodation_javafx.constants.ViewPathConstant;
import org.example.ibb_ecodation_javafx.controller.BootController;
import org.example.ibb_ecodation_javafx.controller.LoginController;
import org.example.ibb_ecodation_javafx.utils.AlertScheduler;
import org.example.ibb_ecodation_javafx.utils.SceneUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

public class GUIStarter extends Application {

    private AnnotationConfigApplicationContext context;
    private SceneUtil sceneUtil;
    @Override
    public void init() throws Exception {
        context = new AnnotationConfigApplicationContext();

        Properties baseProps = new Properties();
        baseProps.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        String profile = baseProps.getProperty("spring.profiles.active");

        context.getEnvironment().setActiveProfiles(profile);

        Properties profileProps = new Properties();
        profileProps.load(getClass().getClassLoader().getResourceAsStream("application-" + profile + ".properties"));

        context.getEnvironment().getPropertySources().addFirst(
                new PropertiesPropertySource("dynamic", profileProps)
        );
        System.out.println("Driver = " + profileProps.getProperty("jdbc.driverClassName"));
        System.out.println("Loaded profile props = " + profileProps);

        // Scan + Refresh
        context.scan("org.example.ibb_ecodation_javafx");
        context.refresh();

        sceneUtil = context.getBean(SceneUtil.class);

        System.out.println("Spring context initialized. Active profiles: " +
                Arrays.toString(context.getEnvironment().getActiveProfiles()));
    }




    @Override
    public void start(Stage primaryStage) throws Exception {
        sceneUtil.loadScene(
                BootController.class,
                primaryStage,
                String.format(ViewPathConstant.FORMAT, "boot"),
                "App"
        );
    }

    @Override
    public void stop() throws Exception {
        if (context != null) {
            context.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}