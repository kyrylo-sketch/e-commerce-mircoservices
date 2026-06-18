package com.wex.order_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String city;
    private String street;
    private String zipcode;
    private String country;

    public Address(String city, String street, String zipcode, String country) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.country = country;
    }
}
