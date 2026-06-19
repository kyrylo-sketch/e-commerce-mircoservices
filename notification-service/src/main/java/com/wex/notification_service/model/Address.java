package com.wex.notification_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Address {
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
