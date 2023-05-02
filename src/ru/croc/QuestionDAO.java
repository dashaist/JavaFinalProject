package ru.croc;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuestionDAO {
    /**
     * Метод для создания вопроса в БД
     * @param question вопрос
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void create(Question question, Connection connection) throws SQLException {
        String sqlCreateQuestion = "INSERT INTO questions (question_text, test_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateQuestion)) {
            statement.setString(1, question.getQuestionText());
            statement.setInt(2, question.getTestId());

            statement.execute();
        }
    }

    /**
     * Метод для получения вопроса из БД по идентификатору
     * @param questionId идентификатор вопроса
     * @param connection соединение с БД
     * @return объект класса Question - вопрос
     * @throws SQLException исключение при работе с БД
     */
    public static Question question(int questionId, Connection connection) throws SQLException {
        String sqlQuestion = "SELECT * FROM questions q WHERE q.question_id = ?";
        Question question = new Question();

        try (PreparedStatement statement = connection.prepareStatement(sqlQuestion)) {
            statement.setInt(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    question.setId(resultSet.getInt("question_id"));
                    question.setQuestionText(resultSet.getString("question_text"));
                    question.setTestId(resultSet.getInt("test_id"));
                }
            }
        }
        return question;
    }

    /**
     * Метод для обновления информации о вопросе в БД
     * @param question обновляемый вопрос
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void update(Question question, Connection connection) throws SQLException {
        String sqlUpdateQuestion = "UPDATE questions q SET q.question_text = ?, q.test_id = ? WHERE q.question_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlUpdateQuestion)) {
            statement.setString(1, question.getQuestionText());
            statement.setInt(2, question.getTestId());
            statement.setInt(3, question.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Метод для удаления вопроса из БД
     * @param questionId идентификатор удаляемого вопроса
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void delete(int questionId, Connection connection) throws SQLException {
        String sqlDeleteQuestion = "DELETE FROM questions q WHERE q.question_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlDeleteQuestion)) {
            statement.setInt(1, questionId);

            statement.executeUpdate();
        }
    }

    /**
     * Метод для получения списка всех вопросов в одной теме
     * @param testId идентифекатор темы
     * @param connection соединение с БД
     * @return список вопросо
     * @throws SQLException исключение при работе с БД
     */
    public static List<Question> getQuestions(int testId, Connection connection) throws SQLException {
        String sqlAllQuestions = "SELECT * FROM questions q WHERE q.test_id = ?";
        List<Question> questions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sqlAllQuestions)) {
            statement.setInt(1, testId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String text = resultSet.getString(2);

                    Question question = new Question(id, text, testId);
                    questions.add(question);
                }

            }
        }
        return questions;
    }

    /**
     * Метод для экспорта вопросов из БД в CSV-файл
     * @param connection соединение с БД
     * @param fileName имя CSV-файла
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    public static void exportQuestionsToCSV(Connection connection, String fileName) throws SQLException, IOException {
        List<Test> tests = TestDAO.getTests(connection);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("id,question_text,test_id\n");

            for (Test test : tests) {
                List<Question> questions = QuestionDAO.getQuestions(test.getId(), connection);

                for (Question question : questions) {
                    writer.append(String.valueOf(question.getId()));
                    writer.append(",");
                    writer.append(question.getQuestionText());
                    writer.append(",");
                    writer.append(String.valueOf(question.getTestId()));
                    writer.append("\n");
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
        Question question = new Question();

        System.out.println("Введите описание нового вопроса: ");
        Scanner in = new Scanner(System.in);
        String questionText = in.nextLine();

        System.out.println("Введите идентификатор темы, к которой вопрос относится: ");
        int testId = in.nextInt();

        question.setQuestionText(questionText);
        question.setTestId(testId);

        QuestionDAO.create(question, connection);
        System.out.println("Вопрос добавлен.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией read
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void readCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор искомого вопроса: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();
        Question question = QuestionDAO.question(id, connection);

        Test test = TestDAO.test(question.getTestId(), connection);

        System.out.println("Искомый вопрос: " + question.getQuestionText() + " относится к теме: " + test.getTopic());
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией update
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void updateCRUD(Connection connection) throws SQLException {
        Question question = new Question();

        System.out.println("Введите новый текст вопроса: ");
        Scanner in = new Scanner(System.in);
        String questionText = in.nextLine();

        System.out.println("Введите идентификатор вопроса, для которого нужно изменить текст на введённый: ");
        int id = in.nextInt();

        System.out.println("Введите идентификатор темы, к которой он должен относится: ");
        int testId = in.nextInt();

        question.setId(id);
        question.setQuestionText(questionText);
        question.setTestId(testId);

        QuestionDAO.update(question, connection);

        System.out.println("Вопрос изменен.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией delete
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void deleteCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор вопроса для удаления: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();

        QuestionDAO.delete(id, connection);
        System.out.println("Вопрос удален.");
    }

    /**
     * Метод для вывода в консоль всех вопросов
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void printAll(Connection connection) throws SQLException {
        List<Test> tests = TestDAO.getTests(connection);

        for (Test oneTest : tests) {
            List<Question> questions = QuestionDAO.getQuestions(oneTest.getId(), connection);

            for (Question oneQuestion : questions) {
                System.out.println(oneQuestion.getId() + " " + oneQuestion.getQuestionText() + " относится к теме " + oneTest.getTopic());
            }
        }
        System.out.println("\n");
    }
}
