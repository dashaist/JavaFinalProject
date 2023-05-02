package ru.croc;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultDAO {
    /**
     * Метод для создания результата в БД
     * @param result результат прохождения теста
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void create(Result result, Connection connection) throws SQLException {
        String sqlCreateResult = "INSERT INTO results (correct_answers_amount, test_data, test_id, user_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateResult)) {

            statement.setInt(1, result.getCorrectAnswersAmount());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(3, result.getTestId());
            statement.setInt(4, result.getUserId());

            statement.execute();
        }
    }

    /**
     * Метод для получения всех результатов пользователя
     * @param connection соединение с БД
     * @param user пользователь
     * @return список всех результатов
     * @throws SQLException исключение при работе с БД
     */
    public static List<Result> results(Connection connection, User user) throws SQLException {
        String sqlAllResults = "SELECT * FROM results WHERE user_id = ?";
        List<Result> results = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sqlAllResults)) {
            statement.setInt(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int resultId = resultSet.getInt("result_id");
                    int amountCorrectAnswers = resultSet.getInt("correct_answers_amount");
                    var dateTime = resultSet.getTimestamp("test_data");
                    int testId = resultSet.getInt("test_id");
                    int userId = resultSet.getInt("user_id");

                    Result result = new Result(resultId, amountCorrectAnswers, dateTime.toLocalDateTime(), testId, userId);
                    results.add(result);
                }
            }
        }
        return results;
    }

    /**
     * Метод для корректного вывод всех рузельтатов пользователя в консоль
     * @param connection соединение с БД
     * @param user пользователь
     * @throws SQLException исключение при работе с БД
     */
    public static void printAllResults(Connection connection, User user) throws SQLException {
        List<Result> results = ResultDAO.results(connection, user);

        if (results.size() == 0) {
            System.out.println("Предыдущих результатов нет.");
        } else {
            System.out.println("Результаты тестов: ");

            for (Result result : results) {

                Instant instant = result.getTestData().atZone(ZoneId.systemDefault()).toInstant();
                java.util.Date date = Date.from(instant);
                Test test = TestDAO.test(result.getTestId(), connection);

                System.out.println("Тест по теме " + test.getTopic() +
                        " был пройдет " + date + " со следующим количеством верных ответов: " +
                        result.getCorrectAnswersAmount());
            }
        }
    }
}
