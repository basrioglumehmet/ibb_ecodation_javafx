package org.example.ibb_ecodation_javafx.repository.preparator;

import org.example.ibb_ecodation_javafx.core.repository.preparator.BatchInsertPreparator;
import org.example.ibb_ecodation_javafx.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserInsertPreparator implements BatchInsertPreparator<User> {
    // = "INSERT INTO users (username, email, password, role, is_verified, is_locked) VALUES(?,?,?,?,?,?)";
    @Override
    public void setValues(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(2, user.getRole().toString());
        ps.setBoolean(2, user.isVerified());
        ps.setBoolean(2, user.isLocked());
    }
}
