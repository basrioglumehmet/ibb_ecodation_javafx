package org.example.ibb_ecodation_javafx.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.service.LanguageService;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.service.UserNoteService;
import org.example.ibb_ecodation_javafx.statemanagement.Store;
import org.example.ibb_ecodation_javafx.statemanagement.state.TranslatorState;
import org.example.ibb_ecodation_javafx.statemanagement.state.UserState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component("alertScheduler")
@RequiredArgsConstructor
public class AlertScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Set<Integer> alertedNoteIds = new HashSet<>();
    private static boolean isRunning = false;
    private static boolean hasShownConnectionError = false;
    private final UserNoteService userNoteService;
    private final LanguageService languageService;

    public void start() {
        // Çalışıyorsa tekrar başlatma
        if (isRunning) return;

        // Zamanlanmış görevleri günlük olarak çalıştır
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Şu anki tarih ve saati al
                LocalDateTime now = LocalDateTime.now();
                // Store örneğini al
                Store store = Store.getInstance();
                // Kullanıcı durumunu al
                UserState userState = store.getCurrentState(UserState.class);
                // Kullanıcı ID'sini al, yoksa -1 döndür
                int userId = userState.getUserDetail() != null ? userState.getUserDetail().getUserId() : -1;

                // Geçerli kullanıcı ID'si yoksa çık
                if (userId == -1) return;

                // Kullanıcıya ait notları al
                List<UserNote> notes = userNoteService.findAllById(userId);

                // Not yoksa zamanlayıcıyı durdur
                if (notes.isEmpty()) {
                    stop(); // Not yoksa zamanlayıcıyı durdur
                    return;
                }

                // Notlar alındıysa bağlantı hatası bayrağını sıfırla
                if (hasShownConnectionError) {
                    hasShownConnectionError = false;
                }

                // Notları kontrol et
                for (UserNote note : notes) {
                    if (note.getReportAt() != null && !alertedNoteIds.contains(note.getId())) {
                        // Timestamp'i LocalDateTime'a çevir ve sadece günü al
                        LocalDateTime reportAt = note.getReportAt().toLocalDateTime().truncatedTo(ChronoUnit.DAYS);
                        // Şu anki tarihi sadece gün bazında al
                        LocalDateTime nowTruncated = now.truncatedTo(ChronoUnit.DAYS);

                        // Sadece tarihleri karşılaştır (saati dikkate alma)
                        if (nowTruncated.isEqual(reportAt) || nowTruncated.isAfter(reportAt)) {
                            // JavaFX UI thread'inde çalıştır
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                // Uyarı başlığını çevir
                                alert.setTitle(languageService.translate("alert.note.title"));
                                // Uyarı üst bilgisini çevir ve not başlığını ekle
                                alert.setHeaderText(languageService.translate("alert.note.header") + note.getHeader());
                                // Not açıklamasını göster
                                alert.setContentText(note.getDescription());
                                // Uyarıyı göster ve bekle
                                alert.showAndWait();
                            });
                            // Not ID'sini uyarılmış listesine ekle
                            alertedNoteIds.add(note.getId());
                        }
                    }
                }

            } catch (Exception e) {
                // Bağlantı hatası gösterilmediyse
                if (!hasShownConnectionError) {
                    hasShownConnectionError = true;
                    // Sistem tepsisinde bildirim göster
                    Platform.runLater(() ->
                            TrayUtil.showTrayNotification("Veritabanı bağlantısı yok. Notlar alınamıyor.", "IBB Bootcamp Uyarı")
                    );
                }
            }
        }, 0, 1, TimeUnit.DAYS); // Günde bir kez çalışacak şekilde ayarlandı

        // Zamanlayıcıyı çalışır duruma getir
        isRunning = true;
    }

    public void stop() {
        // Çalışmıyorsa durdurma
        if (!isRunning) return;

        // Zamanlayıcıyı kapat
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // 5 saniye içinde kapanmazsa zorla kapat
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                // Hata durumunda zorla kapat ve thread'i kes
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        // Uyarılmış not ID'lerini temizle
        alertedNoteIds.clear();
        // Çalışma durumunu kapat
        isRunning = false;
    }

    // Belirli bir notun uyarısını sıfırla
    public void resetAlert(int noteId) {
        alertedNoteIds.remove(noteId);
    }
}