package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer id;

    @Column(name = "first_name")
    @NotNull(message = "Name cannot be null.")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Email(message = "Email must be valid.")
    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "credit")
    @Builder.Default
    private BigDecimal credit = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public void addDebt(BigDecimal amount) {
        this.credit = this.credit.subtract(amount);
    }

    public void addCredit(BigDecimal amount) {
        this.credit = this.credit.add(amount);
    }
}
