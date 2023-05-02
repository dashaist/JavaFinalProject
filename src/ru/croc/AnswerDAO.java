package ru.croc;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnswerDAO {
    /**
     * Метод для добавления нового ответа в БД
     * @param answer ответ
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void create(Answer answer, Connection connection) throws SQLException {
        String sqlCreateAnswer = "INSERT INTO answers (answer_text, is_answer_correct, question_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateAnswer)) {
            statement.setString(1, answer.getAnswerText());
            statement.setBoolean(2, answer.isAnswerCorrect());
            statement.setInt(3, answer.getQuestionId());

            statement.execute();
        }
    }

    /**
     * Метод для поиска ответа в БД по идентификатору
     * @param answerId идентификатор ответ
     * @param connection соединение с бд
     * @return объект класса Answer - ответ
     * @throws SQLException исключение при работе с БД
     */
    public static Answer answer(int answerId, Connection connection) throws SQLException {
        String sqlAnswer = "SELECT * FROM answers a WHERE a.answer_id = ?";
        Answer answer = new Answer();

        try (PreparedStatement statement = connection.prepareStatement(sqlAnswer)) {
            statement.setInt(1, answerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    answer.setAnswerText(resultSet.getString("answer_text"));
                    answer.setAnswerCorrect(resultSet.getBoolean("is_answer_correct"));
                    answer.setQuestionId(resultSet.getInt("question_id"));
                }
            }
        }
        return answer;
    }

    /**
     * Метод для обновления информации об ответе в БД
     * @param answer обновляемый ответ
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void update(Answer answer, Connection connection) throws SQLException {
        String sqlUpdateAnswer = "UPDATE answers a SET a.answer_text = ?, a.is_answer_correct = ?, a.question_id = ? WHERE a.answer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlUpdateAnswer)) {
            statement.setString(1, answer.getAnswerText());
            statement.setBoolean(2, answer.isAnswerCorrect());
            statement.setInt(3, answer.getQuestionId());
            statement.setInt(4, answer.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Метод для удаления ответа из БД
     * @param answerId идентификатор ответа
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void delete(int answerId, Connection connection) throws SQLException {
        String sqlDeleteAnswer = "DELETE FROM answers a WHERE a.answer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlDeleteAnswer)) {
            statement.setInt(1, answerId);

            statement.executeUpdate();
        }
    }

    /**
     * Метод для получения списка всех ответов к одному вопросу
     * @param connection соединение с БД
     * @param questionId идентификатор вопроса
     * @return список ответов
     * @throws SQLException исключение при работе с БД
     */
    public static List<Answer> getAnswers(Connection connection, int questionId) throws SQLException {
        String sqlAllAnswers = "SELECT * FROM answers a WHERE a.question_id = ?";
        List<Answer> answers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sqlAllAnswers)) {
            statement.setInt(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int answerId = resultSet.getInt("answer_id");
                    String text = resultSet.getString("answer_text");
                    boolean isCorrect = resultSet.getBoolean("is_answer_correct");

                    Answer answer = new Answer(answerId, text, isCorrect, questionId);
                    answers.add(answer);
                }
            }
        }
        return answers;
    }

    /**
     * Метод для экспорта данных в CSV-файл
     * @param connection соединение с БД
     * @param fileName имя CSV-файла
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    public static void exportAnswersToCSV(Connection connection, String fileName) throws SQLException, IOException {
        List<Test> tests = TestDAO.getTests(connection);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("id,answer_text,is_answer_correct,question_id\n");

            for (Test test : tests) {
                List<Question> questions = QuestionDAO.getQuestions(test.getId(), connection);

                for (Question question : questions) {
                    List<Answer> answers = AnswerDAO.getAnswers(connection, question.getId());

                    for (Answer answer : answers) {
                        writer.append(String.valueOf(answer.getId()));
                        writer.append(",");
                        writer.append(answer.getAnswerText());
                        writer.append(",");
                        writer.append(String.valueOf(answer.isAnswerCorrect()));
                        writer.append(",");
                        writer.append(String.valueOf(answer.getQuestionId()));
                        writer.append("\n");
                    }
                }
            }

            writer.flush();
        }
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией create
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void createCRUD(Connection connection) throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите описание нового ответа: ");
        String answerText = in.nextLine();

        System.out.println("Введите идентификатор вопроса, к которому ответ относится: ");
        int questionId = in.nextInt();

        System.out.println("Введите true, если ответ является верным, false, если не является: ");
        boolean answerCorrect = in.nextBoolean();

        Answer answer = new Answer();
        answer.setAnswerText(answerText);
        answer.setQuestionId(questionId);
        answer.setAnswerCorrect(answerCorrect);

        AnswerDAO.create(answer, connection);
        System.out.println("Ответ добавлен.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией read
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void readCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор искомого ответа: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();

        Answer answer = AnswerDAO.answer(id, connection);
        Question question = QuestionDAO.question(answer.getQuestionId(), connection);

        System.out.println("Искомый ответ: " + answer.getAnswerText() + " является верным: "
                + answer.isAnswerCorrect() + " для вопроса: " + question.getQuestionText());
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией update
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void updateCRUD(Connection connection) throws SQLException {
        System.out.println("Введите новый текст для ответа: ");
        Scanner in = new Scanner(System.in);
        String answerText = in.nextLine();

        System.out.println("Введите идентификатор ответа для изменения: ");
        int id = in.nextInt();

        System.out.println("Введите true, если ответ верный, false, если неверный: ");
        boolean answerCorrect = in.nextBoolean();

        System.out.println("Введите идентификатор вопроса, к которому он относится: ");
        int questionId = in.nextInt();

        Answer answer = new Answer();
        answer.setId(id);
        answer.setAnswerText(answerText);
        answer.setAnswerCorrect(answerCorrect);
        answer.setQuestionId(questionId);

        AnswerDAO.update(answer, connection);

        System.out.println("Ответ изменен.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией delete
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void deleteCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор ответа для удаления: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();

        AnswerDAO.delete(id, connection);
        System.out.println("Вопрос удален.");
    }

    /**
     * Метод для вывода в консоль всех ответов
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void printAll(Connection connection) throws SQLException {
        List<Test> tests = TestDAO.getTests(connection);

        for (Test oneTest : tests) {
            List<Question> questions = QuestionDAO.getQuestions(oneTest.getId(), connection);

            for (Question oneQuestion : questions) {
                List<Answer> answers = AnswerDAO.getAnswers(connection, oneQuestion.getId());

                for (Answer oneAnswer : answers) {
                    System.out.println(oneAnswer.getId() + " " + oneAnswer.getAnswerText() + " является ответом на вопрос "
                            + oneQuestion.getQuestionText() + " является верным: " + oneAnswer.isAnswerCorrect());
                }
            }
        }
        System.out.println("\n");
    }
}
