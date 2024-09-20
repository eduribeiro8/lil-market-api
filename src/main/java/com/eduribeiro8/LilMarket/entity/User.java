package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "user_name")
    @NotNull(message = "Username cannot be null.")
    @Size(min = 3, message = "Username must have at least 3 characters.")
    private String userName;

    @Column (name = "password")
    @NotNull(message = "Password cannot be null.")
    @Size(min = 4, max = 20, message = "Password must have between 4 and 20 characters.")
    private String password;

    @Column (name = "first_name")
    @NotNull(message = "Name cannot be null.")
    private String firstName;

    @Column (name = "role")
    @NotNull(message = "Role cannot be null.")
    private String role;

    @Column (name = "active")
    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column (updatable = false)
    @CurrentTimestamp
    private Date creationDate;

    @CurrentTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActiveDate;

    public User() {
    }

    public User(String userName, String password, String firstName, boolean active) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.active = active;
        this.role = "ROLE_USER";
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

    public String getRole() {
        return role;
    }
}
