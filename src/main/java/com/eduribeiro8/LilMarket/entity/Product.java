package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "barcode")
    private long barcode;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(name = "quantity_in_stock")
    private int quantity;

    @OneToMany
    private List<SaleItem> saleItemList;

    public Product() {
    }

    public Product(String name, long barcode, String description, double price, ProductCategory productCategory, int quantity) {
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.productCategory = productCategory;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void decreaseQuantity(int quantity){
        this.quantity -= quantity;
    }

    public void updateProduct(Product newProduct){
        this.setName(newProduct.getName());
        this.setBarcode(newProduct.getBarcode());
        this.setQuantity(newProduct.getQuantity());
        this.setPrice(newProduct.getPrice());
        this.setProductCategory(newProduct.getProductCategory());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", productCategory=" + productCategory +
                ", quantity=" + quantity +
                '}';
    }

}
