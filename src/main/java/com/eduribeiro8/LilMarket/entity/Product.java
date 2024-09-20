package com.eduribeiro8.LilMarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;

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

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Product category cannot be null")
    private ProductCategory productCategory;

    @NotNull
    @Min(value = 0, message = "Product quantity must be greater than 0.")
    @Column(name = "quantity_in_stock")
    private int quantity;

    @OneToMany
    private List<SaleItem> saleItemList;

    public Product() {
    }

    public Product(String name, String barcode, String description, BigDecimal price, ProductCategory productCategory, int quantity) {
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.price = price;
        this.productCategory = productCategory;
        this.quantity = quantity;
    }

    public Product(String name, String barcode, String description, double v, ProductCategory productCategory, int quantity) {
        this.name = name;
        this.barcode = barcode;
        this.description = description;
        this.price = BigDecimal.valueOf(v);
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
