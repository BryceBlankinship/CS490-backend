package com.bryceblankinship.CS490.individual.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerDTO {
    private Integer id;
    private Integer storeId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer addressId;
    private String address;
    private String district;
    private Integer cityId;
    private String city;
    private Integer countryId;
    private String country;
    private Integer postalCode;
    private String phone;
    private Boolean active;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdate;
    private List<Integer> rentals;

    // Default constructor
    public CustomerDTO() {
        this.active = true;
        this.storeId = 1;
    }
}
