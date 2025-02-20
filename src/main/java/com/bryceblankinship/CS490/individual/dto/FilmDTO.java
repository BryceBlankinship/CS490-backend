package com.bryceblankinship.CS490.individual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmDTO {

    private Integer id, rentalCount;
    private String title, category;

}
