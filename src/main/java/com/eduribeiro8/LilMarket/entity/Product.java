package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotNull(message = "Product name cannot be null.")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters.")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Product barcode cannot be null.")
    @Size(min = 10, message = "Product barcode must be at least 10 digits.")
    @Column(name = "barcode")
    private String barcode;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.0", message = "Product price must be equal or greater than 0.00")
    @Digits(integer = 10, fraction = 2, message = "Product price must have 2 decimal digits")
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "avg_price")
    private BigDecimal averagePrice;

    @Builder.Default
    @Column(name = "total_quantity")
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "auto_pricing")
    private Boolean autoPricing = true;

    @DecimalMin(value = "0.00", message = "{product.min.profit.margin}")
    @Column(name = "profit_margin")
    private BigDecimal profitMargin;

    @Min(value = 0, message = "{product.min.quantity.in.stock}")
    @Column(name = "min_quantity_in_stock")
    private Integer minQuantityInStock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Product category cannot be null")
    private ProductCategory productCategory;

    @NotNull(message = "{product.unit.type.not.null}")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type")
    private UnitType unitType;

    @Column(name = "is_perishable")
    @Builder.Default
    private Boolean isPerishable = false;

    @Builder.Default
    @Column(name = "alert")
    private Boolean alert = false;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Batch> batches = null;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
