package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sale_items")
public class SaleItem {

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @Id
    @Column(name = "product_id")
    private int product;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    public SaleItem() {
    }

    public SaleItem(Product product, int quantity) {
        this.product = product.getId();
        this.price = product.getPrice();
        this.quantity = quantity;
    }


    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "product=" + product +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
