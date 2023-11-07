package com.eduribeiro8.LilMarket.entity;

public class User {

    private int id;

    private String userName;

    private String password;

    private String firstName;

    private int level;

    private boolean active;

    public User() {
    }

    public User(String userName, String password, String firstName, int level, boolean active) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.level = level;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", level=" + level +
                ", active=" + active +
                '}';
    }
}
