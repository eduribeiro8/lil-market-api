package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Entity
@Table(name = "customer_payments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Payment's customer cannot be null")
    private Customer customer;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    @CreatedDate
    @Column(name = "payment_date")
    private OffsetDateTime paymentDate;

    @Column(name = "notes")
    private String notes;

}
