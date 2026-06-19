package com.wex.product_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "products")
@Data
@NoArgsConstructor
@ToString
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int amount;

    public Product(String name, String description, double price, String category, int amount) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.amount = amount;
    }
}
