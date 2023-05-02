package ru.croc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    /**
     * Метод для добавления пользователя в БД
     * @param user пользователь
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void create(User user, Connection connection) throws SQLException {
        String sqlCreateUser = "INSERT INTO users (login, role) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateUser)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getRole());

            statement.execute();
        }
    }

    /**
     * Метод для получения пользователя из БД по его логину (если пользователя нет в БД, то создаётся и возвращается новый пользователь)
     * @param login логин пользователя
     * @param connection соединенение с БД
     * @return объект класса User - пользователь
     * @throws SQLException исключение при работе с БД
     */
    public static User user(String login, Connection connection) throws SQLException {
        String sqlFindUser = "SELECT * FROM users u WHERE u.login = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlFindUser)) {
            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String role = resultSet.getString("role");

                    User user = new User(userId, login, role);
                    System.out.println("Пользователь найден!");

                    return user;
                    }

                User user = new User(login, "user");
                create(user, connection);

                System.out.println("Пользователь создан!");

                return user;
            }
        }
    }
}
