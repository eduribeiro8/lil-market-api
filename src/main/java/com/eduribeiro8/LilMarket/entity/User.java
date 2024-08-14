package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

@Entity
@Table(name = "users")
public class User {

    @Column (name = "user_name")
    private String userName;

    @Column (name = "password")
    private String password;

    @Column (name = "first_name")
    private String firstName;

    @Column (name = "active")
    private boolean active;

    @Id
    private Long id;

    public User() {
    }

    public User(String userName, String password, String firstName, int level, boolean active) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.active = active;
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
                ", active=" + active +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
