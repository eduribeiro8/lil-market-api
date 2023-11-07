package com.eduribeiro8.LilMarket.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.text.DecimalFormat;

@Entity
@Table(name = "sale_items")
public class SaleItem {

    @ManyToOne
    @JoinColumn(name = "sale_id")
    @JsonBackReference
    private Sale sale;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @Transient
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public SaleItem() {
    }

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.price = product.getPrice() * quantity;
        this.price = Double.parseDouble(decimalFormat.format(getPrice()));
        this.quantity = quantity;
    }


    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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
                "product=" + product.getName() +
                ", price=" + decimalFormat.format(getPrice()) +
                ", quantity=" + quantity +
                '}';
    }

    public void updatePrice() {
        this.price *= this.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SaleItem saleItem = (SaleItem) o;

        if (sale != null ? !sale.equals(saleItem.sale) : saleItem.sale != null) return false;
        return product != null ? product.equals(saleItem.product) : saleItem.product == null;
    }

    @Override
    public int hashCode() {
        int result = sale != null ? sale.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        return result;
    }

}
