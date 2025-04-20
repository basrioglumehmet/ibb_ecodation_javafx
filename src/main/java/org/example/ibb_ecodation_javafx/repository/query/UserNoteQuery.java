package org.example.ibb_ecodation_javafx.repository.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserNoteQuery {

    public static final String CREATE_NOTE = "INSERT INTO user_notes " +
            "(user_id, report_at, header, description, version) " +
            "VALUES (?, ?, ?, ?, ?)";

    public static final String READ_NOTE_BY_ID = "SELECT * FROM user_notes WHERE id=?";

    public static final String READ_ALL_NOTES = "SELECT * FROM user_notes";

    public static final String READ_ALL_NOTES_BY_USER_ID = "SELECT * FROM user_notes WHERE user_id=?";

    public static final String UPDATE_NOTE_BY_ID = "UPDATE user_notes SET " +
            "user_id=?, report_at=?, header=?, description=?, version=version+1 WHERE id=?";

    public static final String SOFT_DELETE_BY_ID = "UPDATE user_notes SET version = version + 1 WHERE id = ? AND version = ?";

    public static final String DELETE_NOTE_BY_ID = "DELETE FROM user_notes WHERE id=?";
    public static final String READ_NOTE_BY_USER_ID = "SELECT * FROM user_notes WHERE user_id=?";
}
