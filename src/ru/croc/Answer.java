package ru.croc;

public class Answer {
    private int id; //Идентификатор ответа
    private String answerText; //Текст ответа
    private boolean answerCorrect; //Является ли ответ верным
    private int questionId; //Идентификатор вопроса, к которому относится ответ

    public Answer() {}

    public Answer(int id, String answerText, boolean answerCorrect, int questionId) {
        this.id = id;
        this.answerText = answerText;
        this.answerCorrect = answerCorrect;
        this.questionId = questionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(boolean answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
