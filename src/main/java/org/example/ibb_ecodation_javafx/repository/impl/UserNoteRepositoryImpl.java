package org.example.ibb_ecodation_javafx.repository.impl;

import org.example.ibb_ecodation_javafx.core.repository.impl.GenericRepositoryImpl;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.model.UserNote;
import org.example.ibb_ecodation_javafx.model.enums.Role;
import org.example.ibb_ecodation_javafx.repository.UserNoteRepository;
import org.example.ibb_ecodation_javafx.repository.query.UserNoteQuery;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserNoteRepositoryImpl extends GenericRepositoryImpl<UserNote, Integer> implements UserNoteRepository {

    public UserNoteRepositoryImpl(DataSource dataSource, Environment environment) {
        super(dataSource, environment);
    }

    @Override
    protected String getTableName() {
        return "user_notes";
    }

    @Override
    protected RowMapper<UserNote> getRowMapper() {
        return new UserNoteRowMapper();
    }

    @Override
    protected String getInsertSql() {
        return UserNoteQuery.CREATE_NOTE;
    }

    @Override
    protected String getUpdateSql() {
        return UserNoteQuery.UPDATE_NOTE_BY_ID;
    }

    @Override
    protected String getSelectByIdSql() {
        return UserNoteQuery.READ_NOTE_BY_ID;
    }

    @Override
    protected String getSelectAllByIdSql() {
        return UserNoteQuery.READ_ALL_NOTES_BY_USER_ID;
    }


    @Override
    protected String getSelectAllSql() {
        return UserNoteQuery.READ_ALL_NOTES;
    }

    @Override
    protected String getDeleteSql() {
        return UserNoteQuery.DELETE_NOTE_BY_ID;
    }

    @Override
    protected Object[] getInsertParams(UserNote entity) {
        return new Object[]{
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getVersion()
        };
    }

    @Override
    protected Object[] getUpdateParams(UserNote entity) {
//[UPDATE user_notes SET user_id=?, report_at=?, header=?, description=?, version=version+1  AND version = ?];
        return new Object[]{
                entity.getUserId(),
                entity.getReportAt(),
                entity.getHeader(),
                entity.getDescription(),
                entity.getId()
        };
    }

    @Override
    protected Integer getVersion(UserNote entity) {
        return entity.getVersion();
    }

    //Inner class
    private static class UserNoteRowMapper implements RowMapper<UserNote> {
        @Override
        public UserNote mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserNote userNote = new UserNote();
            userNote.setId(rs.getInt("id"));
            userNote.setUserId(rs.getInt("user_id"));
            userNote.setReportAt(rs.getTimestamp("report_at"));
            userNote.setHeader(rs.getString("header"));
            userNote.setDescription(rs.getString("description"));
            userNote.setVersion(rs.getInt("version"));
            return userNote;
        }
    }
}
