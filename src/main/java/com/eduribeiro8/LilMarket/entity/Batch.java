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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @Column(name = "batch_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "Product cannot be null")
    @JsonBackReference
    private Product product;

    @Setter
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @NotNull(message = "{supplier.required}")
    private Supplier supplier;

    @Setter
    @ManyToOne
    @JoinColumn(name = "restock_id")
    @NotNull(message = "{restock.required}")
    private Restock restock;

    @Setter
    @Column(name = "batch_code", unique = true)
    @NotNull(message = "Batch code cannot be null")
    private String batchCode;

    @Setter
    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Setter
    @Column(name = "expiration_date")
    @NotNull(message = "Expiration date cannot be null")
    private LocalDate expirationDate;

    @Column(name = "original_quantity", updatable = false)
    private BigDecimal originalQuantity;

    @Setter
    @Builder.Default
    @Column(name = "quantity_in_stock")
    private BigDecimal quantityInStock = BigDecimal.ZERO;

    @Setter
    @Builder.Default
    @Column(name = "quantity_lost")
    private BigDecimal quantityLost = BigDecimal.ZERO;

    @Setter
    @Column(name = "purchase_price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product price must be greater than 0.00")
    @Digits(integer = 10, fraction = 2, message = "Product price must have 2 decimal digits")
    private BigDecimal purchasePrice;

    @CreatedDate
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
