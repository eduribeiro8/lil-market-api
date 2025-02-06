package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @Column(name = "sale_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Sale's customer cannot be null.")
    private Customer customer;

    @Column(name = "sale_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @NotNull(message = "Sale's timestamp cannot be null.")
    private Date timestamp;

    @OneToMany(mappedBy = "sale",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.LAZY)
    @JsonManagedReference
    @NotNull(message = "A sale must have items.")
    private List<SaleItem> items;

    @Column(name = "total_amount")
    private BigDecimal total;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus;

    public Sale() {
        this.total = BigDecimal.valueOf(0);
        this.amountPaid = BigDecimal.valueOf(0);
    }

    public Sale(Customer customer, Date timestamp, List<SaleItem> items, BigDecimal total, BigDecimal amountPaid, PaymentStatus paymentStatus) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.items = items;
        this.total = total;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
    }

    public Sale(int id, Customer customer, Date timestamp, BigDecimal total, BigDecimal amountPaid, PaymentStatus paymentStatus) {
        this.id = id;
        this.customer = customer != null ? new Customer(customer.getId(), customer.getFirstName()) : null;
        this.timestamp = timestamp;
        this.total = total;
        this.items = new ArrayList<>();
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
    }

    public Sale(Sale sale){
        this.id = sale.getId();
        this.customer = sale.getCustomer();
        if(sale.getTimestamp() != null){
            try {
                this.timestamp = sale.getTimestamp();
            }catch (Exception e){
                this.timestamp = new Date();
            }
        }else{
            this.timestamp = new Date();
        }
        this.items = sale.getItems();
        this.total = BigDecimal.valueOf(0);
        this.amountPaid = sale.amountPaid;
        for(SaleItem item : sale.getItems()){
            item.setSale(this);
            this.total = this.total.add(item.getProduct().getPrice().multiply(
                    BigDecimal.valueOf(item.getQuantity()))
            );
        }
        if (amountPaid.equals(this.total)){
            this.paymentStatus = PaymentStatus.PAYMENT_PAID;
        } else if(amountPaid.compareTo(this.total) < 0){
            if (sale.getPaymentStatus() == PaymentStatus.PAYMENT_DEBT){
                this.paymentStatus = PaymentStatus.PAYMENT_DEBT;
            } else {
                this.paymentStatus = PaymentStatus.PAYMENT_PARTLY_PAID;
            }
        } else if(amountPaid.compareTo(this.total) > 0){
            this.paymentStatus = PaymentStatus.PAYMENT_PAID;
            //addCreditToClient
        } else if (amountPaid.equals(BigDecimal.ZERO)){
            this.paymentStatus = PaymentStatus.PAYMENT_PENDING;
        } else{
            this.paymentStatus = sale.getPaymentStatus();
        }
    }

    public void addSaleItem(Product product, int quantity){
        if (items == null){
            items = new ArrayList<>();
            this.total = BigDecimal.valueOf(0);
        }
        SaleItem currentSaleItem = new SaleItem(product, quantity);
        currentSaleItem.setSale(this);

        this.total = total.add(currentSaleItem.getPrice());
        items.add(currentSaleItem);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTotal() {
        return total;
    }


    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public String toString() {
        return "Sale [id=" + id + ", customer=" + customer + ", timestamp=" + timestamp
                + ", items=" + items + ", total=" + total + ", amountPaid=" + amountPaid
                + ", paymentStatus=" + paymentStatus + "]";
    }

    public void updateSale(Sale sale){
        this.setCustomer(sale.getCustomer());
        this.setTimestamp(sale.getTimestamp());
        this.setTotal(sale.getTotal());
        this.setAmountPaid(sale.getAmountPaid());
        this.setPaymentStatus(sale.getPaymentStatus());
        this.setItems(sale.getItems());
    }
}
