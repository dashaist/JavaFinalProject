package ru.croc;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDAO {
    /**
     * Метод для создания теста в БД
     * @param connection соединение с БД
     * @param test тест
     * @throws SQLException исключение при работе с БД
     */
    public static void create(Connection connection, Test test) throws SQLException {
        String sqlCreateTest = "INSERT INTO tests (topic) VALUES (?)";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateTest)) {
            statement.setString(1, test.getTopic());

            statement.execute();
        }
    }

    /**
     * Метод для получения теста из БД по идентификатору
     * @param testId идентификатор теста
     * @param connection соединение с БД
     * @return объект класса Test - тест
     * @throws SQLException исключение при работе с БД
     */
    public static Test test(int testId, Connection connection) throws SQLException {
        String sqlTest = "SELECT * FROM tests t WHERE t.test_id = ?";
        Test test = new Test();

        try (PreparedStatement statement = connection.prepareStatement(sqlTest)) {
            statement.setInt(1, testId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    test.setId(resultSet.getInt("test_id"));
                    test.setTopic(resultSet.getString("topic"));
                }
            }
        }
        return test;
    }

    /**
     * Метод для обновления информации о тесте в БД
     * @param test обновляемый тест
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void update(Test test, Connection connection) throws SQLException {
        String sqlUpdateTest = "UPDATE tests t SET t.topic = ? WHERE t.test_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlUpdateTest)) {
            statement.setString(1, test.getTopic());
            statement.setInt(2, test.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Метод для удаления теста из БД
     * @param testId идентификатор удаляемого теста
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void delete(int testId, Connection connection) throws SQLException {
        String sqlDeleteTest = "DELETE FROM tests t WHERE t.test_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlDeleteTest)) {
            statement.setInt(1, testId);

            statement.executeUpdate();
        }
    }

    /**
     * Метод для получения списка всех тем
     * @param connection соединение с БД
     * @return список тем в БД
     * @throws SQLException исключение при работе с БД
     */
    public static List <Test> getTests(Connection connection) throws SQLException {
        String sqlAllTests = "SELECT * FROM tests";
        List <Test> tests = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            boolean hasResult = statement.execute(sqlAllTests);

            if (hasResult) {
                try (ResultSet resultSet = statement.getResultSet()) {

                    while (resultSet.next()) {
                        int testId = resultSet.getInt("test_id");
                        String topic = resultSet.getString("topic");

                        Test test = new Test(testId, topic);
                        tests.add(test);
                    }

                }
            }
        }
        return tests;
    }

    /**
     * Метод для экспорта тестов из БД в CSV-файл
     * @param connection соединение с БД
     * @param fileName имя CSV-файла
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    public static void exportTestsToCSV(Connection connection, String fileName) throws SQLException, IOException {
        List<Test> tests = TestDAO.getTests(connection);

        try (FileWriter writer = new FileWriter(fileName)){
            writer.append("id,topic\n");

            for (Test test : tests) {
                writer.append(String.valueOf(test.getId()));
                writer.append(",");
                writer.append(test.getTopic());
                writer.append("\n");
            }
            writer.flush();
        }
    }

    /**
     * Метод для выбора пользователем темы для прохождения
     * @param connection соединение с БД
     * @return объект класса Test - выбранная тема
     * @throws SQLException исключение при работе с БД
     */
    public static Test chooseTestTopic(Connection connection) throws SQLException {
        List<Test> tests = TestDAO.getTests(connection);
        Test chosenTest = new Test();

        System.out.println("Введите номер темы, которую хотите пройти.");
        printAll(connection);

        Scanner in = new Scanner(System.in);
        int chosenTestId = in.nextInt();

        boolean testExist = false;
        for(Test test : tests) {
            if(test.getId() == chosenTestId) {
                chosenTest = test;
                testExist = true;
            }
        }

        if(!testExist) {
            System.out.println("Выбранного теста не существет.");
            chosenTest = chooseTestTopic(connection);
        }

        return chosenTest;
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией create
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void createCRUD(Connection connection) throws SQLException {
        Test test = new Test();

        System.out.println("Введите тему нового теста: ");

        Scanner in = new Scanner(System.in);
        String topic = in.nextLine();

        test.setTopic(topic);

        TestDAO.create(connection, test);
        System.out.println("Тема добавлена.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией read
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void readCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор искомого теста: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();

        Test test = TestDAO.test(id, connection);

        System.out.println("Тема искомого теста: " + test.getTopic());
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией update
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void updateCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор существующего теста и новую тему для него через пробел: ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();
        String topic = in.nextLine();

        Test test = new Test(id, topic);
        TestDAO.update(test, connection);
        System.out.println("Тема изменена.");
    }

    /**
     * Метод для корректного взаимодействия администратора в режиме CRUD с операцией delete
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void deleteCRUD(Connection connection) throws SQLException {
        System.out.println("Введите идентификатор теста для удаления: ");

        Scanner in = new Scanner(System.in);
        int id = in.nextInt();

        TestDAO.delete(id, connection);
        System.out.println("Тема удалена.");
    }

    /**
     * Метод для вывода в консоль всех тем
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    public static void printAll(Connection connection) throws SQLException {
        List<Test> tests = TestDAO.getTests(connection);

        for (Test oneTest : tests) {
            System.out.println(oneTest.getId() + " " + oneTest.getTopic());
        }
        System.out.println("\n");
    }
}
