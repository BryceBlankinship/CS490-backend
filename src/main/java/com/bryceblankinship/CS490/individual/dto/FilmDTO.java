package com.bryceblankinship.CS490.individual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmDTO {

    private Integer id, rentalCount, length, releaseYear;
    private String title, category, description, rating;
    private Double rentalRate, replacementCost;
    private Set<String> actors;

}
