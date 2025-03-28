package org.example.ibb_ecodation_javafx.dao;

import javafx.scene.control.Alert;
import org.example.ibb_ecodation_javafx.common.interfaces.IDatabaseConnection;
import org.example.ibb_ecodation_javafx.common.util.AlertUtil;
import org.example.ibb_ecodation_javafx.common.util.QueryUtil;
import org.example.ibb_ecodation_javafx.constant.AlertConstant;
import org.example.ibb_ecodation_javafx.dto.UserDto;
import org.example.ibb_ecodation_javafx.exceptions.RegisterNotFoundException;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.security.BcryptEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.example.ibb_ecodation_javafx.common.util.QueryUtil.selectSingle;

public class UserDao implements Dao<User, UserDto> {
    private static volatile UserDao instance;
    private final Connection connection;
    private UserMapper userMapper;

    private UserDao(IDatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
        this.userMapper = UserMapper.INSTANCE;
    }

    // ✅ Thread-safe Singleton
    public static UserDao getInstance(IDatabaseConnection databaseConnection) {
        if (instance == null) {
            synchronized (UserDao.class) {
                if (instance == null) {
                    instance = new UserDao(databaseConnection);
                }
            }
        }
        return instance;
    }

    @Override
    public void create(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void read(User user) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? LIMIT 1";
        QueryUtil.setConnection(connection);
        var result = selectSingle(sql, User.class, user.getUsername());
        if(result.isPresent() && BcryptEncoder.verifyPassword(user.getPassword(),result.get().getPassword())){
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, AlertConstant.INFORMATION_HEADER,"Giriş Başarılı, OK tuşuna basarak ilerleyin.");
            System.out.println(result.get().toString());
            connection.close();
        }
        else{
            throw new RegisterNotFoundException();
        }
    }


    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public List<User> list() {
        return List.of(); // Implement actual list retrieval logic
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty(); // Implement actual query
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty(); // Implement actual query
    }
}
