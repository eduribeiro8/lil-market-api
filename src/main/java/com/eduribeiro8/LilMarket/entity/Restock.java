package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "restock")
public class Restock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restock_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @NotNull(message = "{supplier.required}")
    @Setter
    private Supplier supplier;

    @Column(name = "restock_invoice")
    @NotBlank(message = "{restock.invoice.required}")
    @Setter
    private String invoice;

    @NotNull(message = "{amount.paid.not.null}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{amount.paid.min.value}")
    @Digits(integer = 10, fraction = 2, message = "{amount.paid.two.digits}")
    @Setter
    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Setter
    @Column(name = "bought_at")
    private LocalDate boughtAt;

    @CreatedDate
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
