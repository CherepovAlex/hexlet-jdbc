package io.pattern_DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class User {
    private Long id;
    private String name;
    private String phone;

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static void main(String[] args) {
        /* Устанавливаем соединение с базой данных var conn = */
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {
            var sql1 = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(100), phone VARCHAR(20))";
            try (var statement1 = conn.createStatement()) {
                statement1.execute(sql1);
            }
            var dao = new UserDAO(conn);

            // Создаем нового пользователя
            var user = new User("Maria", "888888888");
            System.out.println("ID перед сохранением: " + user.getId()); // null
            user.getId(); // null

            // Сохраняем пользователя в БД
            dao.save(user);
            System.out.println("ID после сохранения: " + user.getId()); // Здесь уже выводится сгенерированный id
            user.getId(); // Здесь уже выводится какой-то id

            // Ищем пользователя по ID
            // Возвращается Optional<User>
            var user2 = dao.find(user.getId()).get();
            // user2.getId() == user.getId(); // true
            System.out.println("Сравнение ID: " + (user2.getId() == user.getId())); // true
            // Дополнительные проверки
            System.out.println("Имя пользователя: " + user2.getName());
            System.out.println("Телефон пользователя: " + user2.getPhone());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
class UserDAO {
    private Connection connection;

    public UserDAO(Connection conn) {
        connection = conn;
    }

    public void save(User user) throws SQLException {
        // Если пользователь новый, выполняем вставку
        // Иначе обновляем
        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        } else {
            // Код обновления существующей записи
            var sql = "UPDATE users SET username = ?, phone = ? WHERE id = ?";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.setLong(3, user.getId());
                preparedStatement.executeUpdate();
            }
        }
    }

    public Optional<User> find(Long id) throws SQLException {
        var sql = "SELECT * FROM users WHERE id = ?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                var user = new User(username, phone);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

    // Дополнительный метод для удаления пользователя
    public void delete(Long id) throws SQLException {
        var sql = "DELETE FROM users WHERE id = ?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}