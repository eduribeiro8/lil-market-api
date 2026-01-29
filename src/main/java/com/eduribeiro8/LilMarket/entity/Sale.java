package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @Column(name = "sale_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Sale's customer cannot be null.")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Sale's user cannot be null")
    private User user;

    @CreatedDate
    @Column(name = "sale_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime timestamp;

    @OneToMany(mappedBy = "sale",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SaleItem> items;

    @Column(name = "total_amount")
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "amount_paid")
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;

    @Column(name = "notes")
    private String notes;

    public void addSaleItem(SaleItem item){
        if (this.items == null){
            this.items = new ArrayList<>();
        }
        items.add(item);
        item.setSale(this);

        BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        this.total = this.total.add(itemTotal);
    }

    public void resolvePaymentStatus() {
        if (this.amountPaid == null || this.amountPaid.compareTo(BigDecimal.ZERO) == 0) {
            this.paymentStatus = PaymentStatus.PENDING;
        } else if (this.amountPaid.compareTo(this.total) >= 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else {
            this.paymentStatus = PaymentStatus.PARTIAL;
        }
    }
}
