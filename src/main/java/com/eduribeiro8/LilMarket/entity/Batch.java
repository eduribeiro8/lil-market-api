package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "batches")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @Column(name = "batch_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Product cannot be null")
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @NotNull(message = "{supplier.required}")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "restock_id")
    @NotNull(message = "{restock.required}")
    private Restock restock;

    @Column(name = "batch_code", unique = true)
    @NotNull(message = "Batch code cannot be null")
    private String batchCode;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiration_date")
    @NotNull(message = "Expiration date cannot be null")
    private LocalDate expirationDate;

    @Builder.Default
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock = 0;

    @Builder.Default
    @Column(name = "quantity_lost")
    private Integer quantityLost = 0;

    @Column(name = "purchase_price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product price must be greater than 0.00")
    @Digits(integer = 10, fraction = 2, message = "Product price must have 2 decimal digits")
    private BigDecimal purchasePrice;

    @CreatedDate
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
