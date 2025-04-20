package org.example.ibb_ecodation_javafx.utils;

import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {

    public static byte[] convertImageToByteArray(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    public static Image convertByteArrayToImage(byte[] imageData) throws IOException {
        return new Image(new ByteArrayInputStream(imageData));
    }

    public static Image convertFileToImage(File file) throws IOException {
        return new Image(file.toURI().toString());
    }
}