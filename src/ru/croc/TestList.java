package ru.croc;

import java.util.List;

public class TestList {
    private List<Test> tests; //Списко всех тем, используется для импорта из JSON-файлов

    public TestList() {}

    public TestList(List<Test> tests) {
        this.tests = tests;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }
}
