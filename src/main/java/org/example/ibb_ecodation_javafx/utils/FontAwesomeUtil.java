package org.example.ibb_ecodation_javafx.utils;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.StringProperty;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FontAwesomeUtil {
    public static String getColor(StringProperty glyphIconName){

        String iconName = glyphIconName.get();
        String color = "white"; // Default color

        switch (iconName.toUpperCase()) {

            case "SUCCESS":
                color = "#8dd80a";
                break;
            case "ERROR":
                color = "#ed4245";
                break;
            case "INFO":
                color = "#8dd80a";
                break;
            default:
                color = "white";
                break;
        }

        return color;
    }
    public static FontAwesomeIconView getGlyphIcon(StringProperty glyphIconName) {
        String iconName = glyphIconName.get();
        FontAwesomeIcon icon = FontAwesomeIcon.USER; // Default icon

        switch (iconName.toUpperCase()) {
            case "CALCULATOR":
                icon = FontAwesomeIcon.CALCULATOR;
                break;
            case "PLUS":
                icon = FontAwesomeIcon.PLUS;
                break;
            case "CLOCK":
                icon = FontAwesomeIcon.CLOCK_ALT;
                break;
            case "TRASH":
                icon = FontAwesomeIcon.TRASH;
                break;
            case "USER":
                icon = FontAwesomeIcon.USER;
                break;
            case "SUCCESS":
                icon = FontAwesomeIcon.CHECK_CIRCLE;
                break;
            case "ERROR":
                icon = FontAwesomeIcon.TIMES_CIRCLE;
                break;
            case "INFO":
                icon = FontAwesomeIcon.INFO_CIRCLE;
                break;
            case "CART":
                icon = FontAwesomeIcon.SHOPPING_CART;
                break;
            case "HOME":
                icon = FontAwesomeIcon.HOME;
                break;
            case "NOTIFICATION":
                icon = FontAwesomeIcon.BELL;
                break;
            case "NOTE":
                icon = FontAwesomeIcon.STICKY_NOTE;
                break;
            case "CLOUD_UPLOAD":
                icon = FontAwesomeIcon.CLOUD_UPLOAD;
                break;
            case "COG":
                icon = FontAwesomeIcon.COG;
                break;
            case "EXIT":
                icon = FontAwesomeIcon.SIGN_OUT;
                break;
            case "MAXIMIZE":
                icon = FontAwesomeIcon.ARROWS_ALT;
                break;
            case "CLOSE":
                icon = FontAwesomeIcon.CLOSE;
                break;
            case "QUESTION":
                icon = FontAwesomeIcon.QUESTION;
                break;
            case "MINIMIZE":
                icon = FontAwesomeIcon.WINDOW_MINIMIZE;
                break;
            case "UPLOAD":
                icon = FontAwesomeIcon.CLOUD_UPLOAD;
                break;
            case "PREV":
                icon = FontAwesomeIcon.ARROW_LEFT;
                break;
            case "NEXT":
                icon = FontAwesomeIcon.ARROW_RIGHT;
                break;
            default:
                icon = FontAwesomeIcon.USER;
                break;
        }

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("16px");
        return iconView;
    }
}
