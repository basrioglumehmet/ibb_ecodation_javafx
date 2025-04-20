package org.example.ibb_ecodation_javafx.model.event;

import org.example.ibb_ecodation_javafx.core.result.BootResult;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.enums.BootEventType;
import org.example.ibb_ecodation_javafx.model.enums.NoteEventType;

/**
 * Boot olayını ve türünü saran veri sınıfı.
 *
 * @param eventType Boot türü (SUCCESS, FAILURE,ERROR).
 * @param bootResult      İlgili boot sonucu.
 */
public record BootEvent(BootEventType eventType, BootResult bootResult) {

}
