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
    private Integer id;

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
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0.00")
    @Digits(integer = 10, fraction = 2, message = "Product price must have 2 decimal digits")
    @Column(name = "price")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Product category cannot be null")
    private ProductCategory productCategory;

    @Column(name = "is_perishable")
    @Builder.Default
    private Boolean isPerishable = false;

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


    public int getTotalQuantity(){
        return batches.stream()
                .mapToInt(Batch::getQuantityInStock)
                .sum();
    }
}
