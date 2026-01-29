package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column (name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "user_name", unique = true)
    @NotNull(message = "Username cannot be null.")
    @Size(min = 3, message = "Username must have at least 3 characters.")
    private String username;

    @Column (name = "password")
    @NotNull(message = "Password cannot be null.")
    private String password;

    @Column (name = "first_name")
    @NotNull(message = "Name cannot be null.")
    private String firstName;

    @Column (name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null.")
    private UserRole role;

    @Column (name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column (name = "created_at", updatable = false)
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

}
