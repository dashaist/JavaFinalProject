package ru.croc;

import java.util.List;

public class Question {
    private int id; //Идентификатор вопроса
    private String questionText; //Текст вопроса
    private int testId; //Идентификатор теста, к которому относится вопрос
    private List<Answer> answers; //Список ответов на вопрос, используется для импорта из JSON

    public Question() {}

    public Question(int id, String questionText, int testId) {
        this.id = id;
        this.questionText = questionText;
        this.testId = testId;
    }

    public Question(int id, String questionText, int testId, List<Answer> answers) {
        this.id = id;
        this.questionText = questionText;
        this.testId = testId;
        this.answers = answers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
