package org.example.ibb_ecodation_javafx.common.components;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnimatedAvatar extends Avatar {
    private final List<Image> images = new ArrayList<>(); // Resim listesi
    private int currentIndex = 0;
    private Timeline timeline;

    public AnimatedAvatar(double size, List<URL> imageUrls) {
        super(size);
        loadImages(imageUrls);
    }

    /**
     * Resimleri Tek tek yüklemelisin.
     * @param imageUrls
     */
    private void loadImages(List<URL> imageUrls) {
        Task<Void> loadImagesTask = new Task<>() {
            @Override
            protected Void call() {
                for (URL url : imageUrls) {
                    images.add(new Image(url.toExternalForm(), false));
                }
                return null;
            }
        };

        // Resimler başarıyla yüklendiğinde animasyonu tetikle
        loadImagesTask.setOnSucceeded(e -> {
            if (!images.isEmpty()) {
                setAvatarImage(images.get(0)); // İlk resmi göster
                startAnimation(); //Animasyonu başlat
            }
        });

        //Thread oluştur diğer işlemleri bloklamasın.
        new Thread(loadImagesTask).start();
    }

    /**
     * Bu metot timeline ve keyframe kullanarak lag yapmayan altyapı desteği sağlamaktadır.
     * Index bazlı resimleri yükler ve sonsuz döngü şeklinde devam eder.
     */
    private void startAnimation() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            if (!images.isEmpty()) {
                currentIndex = (currentIndex + 1) % images.size();
                setAvatarImage(images.get(currentIndex));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //Avatar Circle getirilir ve fill ile resim verilir.
    private void setAvatarImage(Image image) {
        getAvatarCircle().setFill(new ImagePattern(image));
    }
    //Animasyonu durdurmak için yardımcı metot.
    public void stopAnimation() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
