package org.example.ibb_ecodation_javafx.model.event;

import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.enums.NoteEventType;

/**
 * Not olayını ve türünü saran veri sınıfı.
 *
 * @param eventType Olay türü (CREATE, UPDATE, DELETE).
 * @param note      İlgili kullanıcı notu.
 */
public record NoteEvent(NoteEventType eventType, UserNote note) {

}
