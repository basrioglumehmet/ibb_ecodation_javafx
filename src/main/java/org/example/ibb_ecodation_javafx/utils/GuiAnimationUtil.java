package org.example.ibb_ecodation_javafx.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;
import lombok.experimental.UtilityClass;

/**
 * GUI elemanlarına animasyon eklemek için yardımcı sınıf.
 * Ölçek ve saydamlık animasyonlarını içerir.
 */
@UtilityClass
public class GuiAnimationUtil {

    /**
     * Bir Parent nesnesine ölçek animasyonu (büyüme) uygular.
     * Nesne sıfır boyutundan normal boyutuna 0.5 saniyede büyür.
     *
     * @param parent Animasyon uygulanacak kök düğüm
     */
    public static void runAnimation(Parent parent) {
        // Ölçek animasyonu (0'dan 1'e büyüme)
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), parent);
        scaleTransition.setFromX(0); // Başlangıç X ölçeği (sıfır)
        scaleTransition.setFromY(0); // Başlangıç Y ölçeği (sıfır)
        scaleTransition.setToX(1);   // Bitiş X ölçeği (normal boyut)
        scaleTransition.setToY(1);   // Bitiş Y ölçeği (normal boyut)

        scaleTransition.setCycleCount(1);    // Animasyon bir kere çalışır
        scaleTransition.setAutoReverse(false); // Geri dönüş yok

        // Animasyonu başlat
        scaleTransition.play();
    }
    /**
     * Fare olaylarına (hover/exit) bağlı olarak bir düğüm için ölçek animasyonu çalıştırır.
     *
     * @param node Animasyon uygulanacak düğüm (Node)
     * @param hoverType Animasyon tipi (HOVERING: üzerine gelme, EXIT: çıkma)
     */
    public static void runAnimationForNode(Node node, HoverType hoverType) {
        // Ortak ScaleTransition ayarları
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), node);
        scaleTransition.setCycleCount(1);    // Animasyon bir kere çalışır
        scaleTransition.setAutoReverse(false); // Geri dönüş yok

        switch (hoverType) {
            case HOVERING -> {
                // Üzerine gelindiğinde büyüt (1'den 1.1'e)
                scaleTransition.setFromX(1.0);  // Normal boyut
                scaleTransition.setFromY(1.0);
                scaleTransition.setToX(0.93);    // %10 büyüt
                scaleTransition.setToY(0.93);
            }
            case EXIT -> {
                // Çıkıldığında normale dön (1.1'den 1'e)
                scaleTransition.setFromX(0.93);  // Büyütülmüş boyut
                scaleTransition.setFromY(0.93);
                scaleTransition.setToX(1.0);    // Normal boyut
                scaleTransition.setToY(1.0);
            }
        }

        // Animasyonu başlat
        scaleTransition.play();
    }

    /**
     * Fare olay tiplerini tanımlayan enum.
     */
    public enum HoverType {
        HOVERING,  // Fare düğümün üzerine geldiğinde
        EXIT       // Fare düğümden çıktığında
    }
    /**
     * Bir Parent nesnesine saydamlık animasyonu uygular.
     * Nesne tamamen saydamdan (opacity 0) tamamen görünür hale (opacity 1) 0.5 saniyede geçer.
     *
     * @param parent Animasyon uygulanacak kök düğüm
     */
    public static void runOpacityAnimation(Parent parent) {
        // Saydamlık animasyonu (0'dan 1'e geçiş)
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), parent);
        fadeTransition.setFromValue(0.0); // Başlangıç saydamlığı (tamamen saydam)
        fadeTransition.setToValue(1.0);   // Bitiş saydamlığı (tamamen görünür)
        fadeTransition.setCycleCount(1);  // Animasyon bir kere çalışır
        fadeTransition.setAutoReverse(false); // Geri dönüş yok

        // Animasyonu başlat
        fadeTransition.play();
    }
}