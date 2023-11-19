package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

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
    private Customer customer;

    @Column(name = "timestamp")
    private String timestamp;

    @OneToMany(mappedBy = "sale",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<SaleItem> items;

    @Column(name = "total_amount")
    private double total;

    public Sale() {
        this.total = 0;
    }

    public Sale(Customer customer, String timestamp, List<SaleItem> items, double total) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.items = items;
        this.total = total;
    }

    public void addSaleItem(Product product, int quantity){
        if (items == null){
            items = new ArrayList<>();
        }
        System.out.println("\nAdding item: " + product);

        SaleItem currentSaleItem = new SaleItem(product, quantity);
        currentSaleItem.setSale(this);

        total += currentSaleItem.getPrice();
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
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
