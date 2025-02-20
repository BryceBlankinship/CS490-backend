package com.bryceblankinship.CS490.individual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorDTO {

    private Integer id, movies;
    private String firstName, lastName;

}
