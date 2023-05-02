package ru.croc;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import static java.lang.System.exit;

public class TestingSystem {
    static final String url = "jdbc:h2:tcp://localhost:9092/~/Desktop/EnglishTestingSystem/db/EnglishTestingSystemDB";
    static boolean adminAccess = false;
    static Result tempResult = new Result();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(url, "admin", "admin")) {

            Scanner in = new Scanner(System.in);
            System.out.println("Введите свой логин для входа. (Если вы не регистрировались ранее, ваш профиль будет автоматически создан)");
            String login = in.nextLine();
            User user = authorization(connection, login);

            if (!adminAccess) {
                while (true) {
                    interactingWithUser(connection, user);
                }
            } else {
                while (true) {
                    interactingWithAdmin(connection, user);
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
        }
    }

    /**
     * Метод начального взаимодействия с обычным пользователем
     * @param connection соединение с БД
     * @param user пользователь
     * @throws SQLException исключение при работе с БД
     */
    private static void interactingWithUser (Connection connection, User user) throws SQLException {
        System.out.println("Введите 1 для просмотра своих предыдущих результатов.\n" +
                "Введите 2 для начала тестирования.\n" +
                "Введите 3 для закрытия программы. ");

        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1 :
                ResultDAO.printAllResults(connection, user);
                break;
            case 2 :
                while (true) {
                    if(!testingMode(connection)) {
                        System.out.println("Тестирование окончено!");
                        break;
                    }
                }
                break;
            case 3 :
                exit (0);
            default :
                interactingWithUser(connection, user);
        }
    }

    /**
     * Метод начального взаимодействия с администратором
     * @param connection соединение с БД
     * @param user пользователь
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    private static void interactingWithAdmin (Connection connection, User user) throws SQLException, IOException {
        System.out.println("Введите 1 для перехода в режим тестирования.\n" +
                "Введите 2 для перехода в режим CRUD.\n" +
                "Введите 3 для экспорта тестов в CSV-файл.\n" +
                "Введите 4 для иморта тестов из JSON-файла.\n" +
                "Введите 5 для закрытия программы."
        );

        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                while (true) {
                    interactingWithUser(connection, user);
                }
            case 2:
                crudMode(connection);
                break;
            case 3:
                exportToCSV(connection);
                break;
            case 4:
                importFromJSON(connection);
                break;
            case 5:
                exit(0);
            default:
                interactingWithAdmin(connection, user);
                break;
        }
    }

    /**
     * Метод авторизации и наделения правами пользователя
     * @param connection соединение с БД
     * @param login логин пользователя
     * @return объект класса User - найденный или созданный пользователь в БД
     * @throws SQLException исключение при работе с БД
     */
    private static User authorization(Connection connection, String login) throws SQLException {
        User user = UserDAO.user(login, connection);

        if(user.getRole().equals("admin")) {
            adminAccess = true;
        }

        tempResult.setUserId(user.getId());
        return user;
    }

    /**
     * Метод экспорта тестов, вопросов и ответов в CSV-файл
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    private static void exportToCSV(Connection connection) throws IOException, SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите название таблицы, которую необходимо экспортировать в csv: \n" +
                "Доступны таблицы tests, questions, answers.");
        String tableName = in.nextLine();

        System.out.println("Введите путь к файлу, в который должно произойти экспортирование: ");
        String fileName = in.nextLine();

        switch (tableName.toLowerCase()) {
            case "tests" :
                TestDAO.exportTestsToCSV(connection, fileName);
                break;
            case "questions" :
                QuestionDAO.exportQuestionsToCSV(connection, fileName);
                break;
            case "answers" :
                AnswerDAO.exportAnswersToCSV(connection, fileName);
                break;
            default :
                System.out.println("Неправильное название таблицы!");
                exportToCSV(connection);
                break;
        }
    }

    /**
     * Метод для испорта тестов из JSON-файла
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     * @throws IOException исключение при доступе к данным с помощью потоков, файлов и каталогов
     */
    private static void importFromJSON(Connection connection) throws IOException, SQLException {
        System.out.println("Введите абсолютный путь до json-файла:");

        Scanner in = new Scanner(System.in);
        String fileName = in.nextLine();

        ObjectMapper objectMapper = new ObjectMapper();
        TestList testList = objectMapper.readValue(new File(fileName), TestList.class);

        for (Test test : testList.getTests()) {
            TestDAO.create(connection, test);

            for (Question question : test.getQuestions()) {
                QuestionDAO.create(question, connection);

                for (Answer answer : question.getAnswers()) {
                    AnswerDAO.create(answer, connection);
                }
            }
        }
    }

    /**
     * Метод для режима тестирования
     * @param connection соединение с БД
     * @return true для продолжения тестирования, false для завершения тестирования
     * @throws SQLException исключение при работе с БД
     */
    private static boolean testingMode(Connection connection) throws SQLException {
        Test test = TestDAO.chooseTestTopic(connection);
        List <Question> questions = QuestionDAO.getQuestions(test.getId(), connection);

        tempResult.setTestId(test.getId());

        for (Question question : questions) {
            if(!askOneQuestion(connection, question)) {
                 return makingChoiceAfterTestEnding(connection, test.getTopic());
            }
        }

        return true;
    }

    /**
     * Метод для CRUD режима
     * @param connection соединение с БД
     * @throws SQLException исключение при работе с БД
     */
    private static void crudMode(Connection connection) throws SQLException {
        System.out.println("Введите через пробел название таблицы для изменения и вид изменения (C, R, U, D, all)\n" +
                "(Где C - создание записи, R - получение записи, U - обновление записи, D - удаление записи, all - вывод все таблицы)\n" +
                "Для редактирования доступны следующие таблицы:\n" +
                "tests, questions, answers.");

        Scanner in = new Scanner(System.in);
        String tableName = in.next();
        String operationType = in.next();

        switch (tableName.toLowerCase()) {
            case "tests" :
                workWithTestsTable(connection, operationType);
                break;
            case "questions" :
                workWithQuestionsTable(connection, operationType);
                break;
            case "answers" :
                workWithAnswersTable(connection, operationType);
                break;
            default :
                System.out.println("Проверьте написание названия таблицы.");
                crudMode(connection);
                break;
        }
    }

    /**
     * Метод для работы с тестами в CRUD режиме
     * @param connection соединение с БД
     * @param operationType тип операции CRUD
     * @throws SQLException исключение при работе с БД
     */
    private static void workWithTestsTable(Connection connection, String operationType) throws SQLException {
        switch (operationType.toLowerCase()) {
            case "c" :
                TestDAO.createCRUD(connection);
                break;
            case "r" :
                TestDAO.readCRUD(connection);
                break;
            case "u" :
                TestDAO.updateCRUD(connection);
                break;
            case "d" :
                TestDAO.deleteCRUD(connection);
                break;
            case "all" :
                TestDAO.printAll(connection);
                break;
            default :
                System.out.println("Проверьте правильность написания режима изменения.");
                crudMode(connection);
        }
    }

    /**
     * Метод для работы с вопросами в CRUD режиме
     * @param connection соединение с БД
     * @param operationType тип операции CRUD
     * @throws SQLException исключение при работе с БД
     */
    private static void workWithQuestionsTable(Connection connection, String operationType) throws SQLException {
        switch (operationType.toLowerCase()) {
            case "c" :
                QuestionDAO.createCRUD(connection);
                break;
            case "r" :
                QuestionDAO.readCRUD(connection);
                break;
            case "u" :
                QuestionDAO.updateCRUD(connection);
                break;
            case "d" :
                QuestionDAO.deleteCRUD(connection);
                break;
            case "all" :
                QuestionDAO.printAll(connection);
                break;
            default :
                System.out.println("Проверьте правильность написания режима изменения.");
                crudMode(connection);
        }
    }

    /**
     * Метод для работы с ответами в CRUD режиме
     * @param connection соединение с БД
     * @param operationType тип операции CRUD
     * @throws SQLException исключение при работе с БД
     */
    private static void workWithAnswersTable(Connection connection, String operationType) throws SQLException {
        switch (operationType.toLowerCase()) {
            case "c" :
                AnswerDAO.createCRUD(connection);
                break;
            case "r" :
                AnswerDAO.readCRUD(connection);
                break;
            case "u" :
                AnswerDAO.updateCRUD(connection);
                break;
            case "d" :
                AnswerDAO.deleteCRUD(connection);
                break;
            case "all" :
                AnswerDAO.printAll(connection);
                break;
            default :
                System.out.println("Проверьте правильность написания режима изменения.");
                crudMode(connection);
        }
    }

    /**
     * Метод для выбора дальнейшего действия после завершения прохождения тестирования по одной теме
     * @param connection соединение с БД
     * @param testTopic тема оконченого теста
     * @return false для завершения всего тестирования, true для выбора другой темы
     * @throws SQLException исключение при работе с БД
     */
    private static boolean makingChoiceAfterTestEnding(Connection connection, String testTopic) throws SQLException {
        System.out.println("Тестирование по теме " + testTopic + " окончено!\n" +
                "Введите 1, если хотите выбрать другую тему для тестирования.\n" +
                "Введите 2, если хотите окончить тестирование."
        );
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                return testingMode(connection);
            case 2:
                return false;
            default:
                return makingChoiceAfterTestEnding(connection, testTopic);
        }
    }

    /**
     * Метод для вывода и обработки вопроса из выбранной пользователем темы
     * @param connection соединение с БД
     * @param question вопрос из выбранной темы
     * @return false для завершения тестирования по одной теме, true для вывода следующего вопроса
     * @throws SQLException исключение для работы с БД
     */
    private static boolean askOneQuestion(Connection connection, Question question) throws SQLException {
        System.out.println(question.getQuestionText());
        System.out.println("Выберите из предложенных слов пропущенное (пропущенные).");

        List<Answer> answers = AnswerDAO.getAnswers(connection, question.getId());

        for (Answer answer : answers) {
            System.out.println(answer.getAnswerText());
        }

        Scanner in = new Scanner(System.in);
        String chosenAnswer = in.nextLine();

        for (Answer answer : answers) {
            if(chosenAnswer.equals(answer.getAnswerText()) && answer.isAnswerCorrect()) {
                tempResult.setCorrectAnswersAmount(tempResult.getCorrectAnswersAmount() + 1);
                return makingChoiceAfterTrueAnswer(connection);

            } else if(chosenAnswer.equals(answer.getAnswerText()) && !answer.isAnswerCorrect()) {
                return makingChoiceAfterFalseAnswer(connection, question);
            }
        }

        System.out.println("Выбранного ответа не существет");
        return askOneQuestion(connection, question);
    }

    /**
     * Метод для выбора дальнейшего действия после выбора правильного ответа на вопрос
     * @param connection соединение с БД
     * @return true для вывода следующего вопроса, false для завершения тестирования по данной теме
     * @throws SQLException исключение при работе с БД
     */
    private static boolean makingChoiceAfterTrueAnswer(Connection connection) throws SQLException {
        System.out.println("Ответ верный. Выберите 1, если хотите увидеть следующий вопрос.\n" +
                "Выберите 2, если хотите закончить тестирование по данной теме."
        );

        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                return true;
            case 2:
                ResultDAO.create(tempResult, connection);
                tempResult = new Result();
                return false;
            default:
                return makingChoiceAfterTrueAnswer(connection);
        }
    }

    /**
     * Метод для выбора дальнейшего действия после выбора неправильного ответа на вопрос
     * @param connection соединение с БД
     * @param question вопрос
     * @return true для продолжения тестирования, false для завершения тестирования по теме,
     * askOneQuestion(connection, question) для повтора вопроса,
     * makingChoiceAfterFalseAnswer(connection, question) если пользователь ввёл некорректный ответ
     * @throws SQLException исключения с БД
     */
    private static boolean makingChoiceAfterFalseAnswer(Connection connection, Question question) throws SQLException {
        System.out.println("Ответ неверный. Возможно, вы совершили грамматическую ошибку в ответе.\n" +
                "Выберите 1, если хотите увидеть следующий вопрос.\n" +
                "Выберите 2, если хотите ответить на текущий вопрос ещё раз.\n" +
                "Выберите 3, если хотите закончить тестирование по данной теме."
        );

        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                return true;
            case 2:
                return askOneQuestion(connection, question);
            case 3:
                return false;
            default:
                return makingChoiceAfterFalseAnswer(connection, question);
        }
    }
}
