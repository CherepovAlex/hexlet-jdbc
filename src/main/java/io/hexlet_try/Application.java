package io.hexlet_try;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException {
        // Соединение с базой данных тоже надо отслеживать
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {

            var sql1 = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            try (var statement1 = conn.createStatement()) {
                statement1.execute(sql1);
            }

            var sql2 = "INSERT INTO users (username, phone) VALUES ('tommy', '123456789')";
            try (var statement2 = conn.createStatement()) {
                statement2.executeUpdate(sql2);
            }

            var sql3 = "SELECT * FROM users";
            try (var statement3 = conn.createStatement()) {
                var resultSet = statement3.executeQuery(sql3);
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("username"));
                    System.out.println(resultSet.getString("phone"));
                }
            }
        }
    }
}
