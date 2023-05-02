package ru.croc;

public class User {
    private int id; //Идентификатор пользователя
    private String login; //Логин пользователя
    private String role; //Роль пользователя в БД (у пользователя с логином "admin" роль "admin", у всех остальных роль "user")

    public User(String login, String role) {
        this.login = login;
        this.role = role;
    }

    public User(int id, String login, String role) {
        this.id = id;
        this.login = login;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
