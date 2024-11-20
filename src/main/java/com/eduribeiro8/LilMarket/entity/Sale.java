package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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
    @JsonIgnore
//    @NotNull(message = "Sale's timestamp cannot be null.")
    private Date timestamp;

    @OneToMany(mappedBy = "sale",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.EAGER)
    @JsonManagedReference
    @NotNull(message = "A sale must have items.")
    private List<SaleItem> items;

    @Column(name = "total_amount")
    private BigDecimal total;

    public Sale() {
        this.total = BigDecimal.valueOf(0);
    }

    public Sale(Customer customer, Date timestamp, List<SaleItem> items, BigDecimal total) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.items = items;
        this.total = total;
    }

    public Sale(Sale sale){
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
        for(SaleItem item : sale.getItems()){
            item.setSale(this);
            this.total = this.total.add(item.getProduct().getPrice().multiply(
                    BigDecimal.valueOf(item.getQuantity()))
            );
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

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", customer=" + customer.getFirstName() +
                ", timestamp=" + timestamp +
                ", items=" + items +
                ", total=" + total +
                '}';
    }
}
