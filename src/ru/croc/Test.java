package ru.croc;

import java.util.List;

public class Test {
    private int id; //Идентификатор теста
    private String topic; //Тема теста
    private List<Question> questions; //Список всех вопросов в тесте, используется для испорта из JSON-файла

    public Test() {}

    public Test(int id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
