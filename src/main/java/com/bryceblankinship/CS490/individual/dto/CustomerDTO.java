package com.bryceblankinship.CS490.individual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private Integer id, storeId, addressId, cityId, postalCode, countryId;
    private String firstName, lastName, email, address, district, phone, city, country;
    private Boolean active;
    private LocalDateTime createDate, lastUpdate;

}
