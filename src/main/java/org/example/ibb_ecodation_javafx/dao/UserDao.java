package org.example.ibb_ecodation_javafx.dao;

import javafx.scene.control.Alert;
import org.example.ibb_ecodation_javafx.common.interfaces.IDatabaseConnection;
import org.example.ibb_ecodation_javafx.common.util.AlertUtil;
import org.example.ibb_ecodation_javafx.common.util.QueryUtil;
import org.example.ibb_ecodation_javafx.constant.AlertConstant;
import org.example.ibb_ecodation_javafx.dto.UserDto;
import org.example.ibb_ecodation_javafx.enums.Role;
import org.example.ibb_ecodation_javafx.exceptions.RegisterNotFoundException;
import org.example.ibb_ecodation_javafx.mapper.UserMapper;
import org.example.ibb_ecodation_javafx.model.User;
import org.example.ibb_ecodation_javafx.security.BcryptEncoder;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.example.ibb_ecodation_javafx.common.util.QueryUtil.selectSingle;

public class UserDao implements Dao<User, UserDto> {
    private static volatile UserDao instance;
    private final Connection connection;
    private final UserMapper userMapper;

    private UserDao(IDatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
        this.userMapper = UserMapper.INSTANCE;
    }

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
        if (isEmailExists(user.getEmail())) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"E-posta zaten mevcut.");
            return;
        }

        // Kullanıcı adı ve e-posta kontrolü
        if (isUsernameExists(user.getUsername())) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı adı zaten mevcut.");
            return;
        }

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, BcryptEncoder.hashPassword(user.getPassword()));
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException | NoSuchAlgorithmException exception) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı Oluşturulamadı");
        }
    }

    public void login(User user) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? LIMIT 1";
        QueryUtil.setConnection(connection);
        var result = selectSingle(sql, User.class, user.getUsername());
        if (result.isPresent() && BcryptEncoder.verifyPassword(user.getPassword(), result.get().getPassword())) {
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, AlertConstant.INFORMATION_HEADER, "Giriş Başarılı, OK tuşuna basarak ilerleyin.");
        } else {
            throw new RegisterNotFoundException();
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, BcryptEncoder.hashPassword(user.getPassword()));
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException | NoSuchAlgorithmException exception) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı Güncelleme Başarısız");
        }
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı Silme Başarısız");
        }
    }

    @Deprecated
    @Override
    public List<User> list() {
        return List.of();
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt("id"))
                        .username(resultSet.getString("username"))
                        .email(resultSet.getString("email"))
                        .role(Role.fromString(resultSet.getString("role")))
                        .build();
                return Optional.of(user);
            }
        } catch (SQLException exception) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı adıyla kullanıcı bulunamadı");
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt("id"))
                        .username(resultSet.getString("username"))
                        .email(resultSet.getString("email"))
                        .role(Role.fromString(resultSet.getString("role")))
                        .build();
                return Optional.of(user);
            }
        } catch (SQLException exception) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, AlertConstant.INFORMATION_HEADER,"Kullanıcı id ile kullanıcı bulunamadı");
        }
        return Optional.empty();
    }

    @Override
    public void read(User item) throws SQLException {
        System.out.println("Kullanıcı detayları okunuyor: " + item.getUsername());
        Optional<User> userOptional = findById(item.getId());
        userOptional.ifPresentOrElse(
                user -> System.out.println("Kullanıcı detayları: " + user),
                () -> System.out.println("ID ile kullanıcı bulunamadı: " + item.getId())
        );
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // kayıt varsa true döner
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // hata varsa güvenlik için false yerine true döneriz
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // hata varsa true dön ki işlem durdurulsun
        }
    }
}
