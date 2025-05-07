package io.Statements;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public static void main(String[] args) throws SQLException {
        // Соединение с базой данных тоже надо отслеживать
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {

            var sql1 = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            try (var statement1 = conn.createStatement()) {
                statement1.execute(sql1);
            }
            String[][] users = {
                    {"Sarah", "33333333"},
                    {"John", "44444444"},
                    {"Mike", "55555555"},
                    {"Anna", "66666666"}
            };

            var sql2 = "INSERT INTO users (username, phone) VALUES (?,?)";
            try (var preparedStatement = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
                // Перебираем всех пользователей и добавляем их через один PreparedStatement
                for (String[] user : users) {
                    preparedStatement.setString(1, user[0]); // username
                    preparedStatement.setString(2, user[1]); // phone
                    preparedStatement.addBatch(); // Добавляем в batch
                }
                // Выполняем все операции вставки разом
                int[] updateCounts = preparedStatement.executeBatch();
                // Получаем сгенерированные ключи
                var generatedKeys = preparedStatement.getGeneratedKeys();
                System.out.println("Generated IDs:");
                while (generatedKeys.next()) {
                    System.out.println(generatedKeys.getLong(1));
                }
            }
            // Удаление пользователя по имени (например, "John")
            var deleteSql = "DELETE FROM users WHERE username = ?";
            try (var deleteStatement = conn.prepareStatement(deleteSql)) {
                deleteStatement.setString(1, "John"); // Удаляем пользователя с именем "John"
                int rowsDeleted = deleteStatement.executeUpdate();
                System.out.println("Deleted " + rowsDeleted + " user(s) with name 'John'");
            }

            var sql3 = "SELECT * FROM users";
            try (var statement3 = conn.createStatement()) {
                var resultSet = statement3.executeQuery(sql3);
                while (resultSet.next()) {
                    System.out.println("Username: " + resultSet.getString("username") +
                            ", Phone: " + resultSet.getString("phone"));
                }
            }
        }
    }
}
