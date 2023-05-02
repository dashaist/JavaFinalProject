package ru.croc;

import java.time.LocalDateTime;

public class Result {
    private int id; //Идентификатор результата
    private int correctAnswersAmount = 0; //Количество верных ответов
    private LocalDateTime testData; //Время окончания теста
    private int testId; //Идентификатор теста
    private int userId; //Идентификатор пользователя

    public Result() {}

    public Result(int id, int correctAnswersAmount, LocalDateTime testData, int testId, int userId) {
        this.id = id;
        this.correctAnswersAmount = correctAnswersAmount;
        this.testData = testData;
        this.testId = testId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorrectAnswersAmount() {
        return correctAnswersAmount;
    }

    public void setCorrectAnswersAmount(int correctAnswersAmount) {
        this.correctAnswersAmount = correctAnswersAmount;
    }

    public LocalDateTime getTestData() {
        return testData;
    }

    public void setTestData(LocalDateTime testData) {
        this.testData = testData;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
