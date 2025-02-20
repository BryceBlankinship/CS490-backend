package com.bryceblankinship.CS490.individual.services;

import com.bryceblankinship.CS490.individual.dto.ActorDTO;
import com.bryceblankinship.CS490.individual.dto.FilmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    public List<FilmDTO> getTopFilms() {
        List<FilmDTO> res = new ArrayList<>();
        jdbc.query("SELECT f.film_id, f.title, c.name AS category, COUNT(r.rental_id) AS rental_count FROM rental r JOIN inventory i ON r.inventory_id = i.inventory_id JOIN film f ON i.film_id = f.film_id JOIN film_category fc ON f.film_id = fc.film_id JOIN category c ON fc.category_id = c.category_id GROUP BY f.film_id, f.title, c.name ORDER BY rental_count DESC LIMIT 5;", rs -> {
            FilmDTO dto = FilmDTO.builder()
                    .id(rs.getInt("film_id"))
                    .title(rs.getString("title"))
                    .category(rs.getString("category"))
                    .rentalCount(rs.getInt("rental_count")).
                    build();

            res.add(dto);
        });

        return res;
    }

    public List<ActorDTO> getTopActors(){
        List<ActorDTO> res = new ArrayList<>();

        jdbc.query("SELECT actor.actor_id, actor.first_name, actor.last_name, COUNT(*) as movies FROM actor " +
                "JOIN film_actor on actor.actor_id = film_actor.actor_id " +
                "GROUP BY actor.actor_id ORDER BY (COUNT(*)) DESC LIMIT 5;", rs -> {
            ActorDTO dto = ActorDTO.builder()
                    .id(rs.getInt("actor.actor_id"))
                    .firstName(rs.getString("actor.first_name"))
                    .lastName(rs.getString("actor.last_name"))
                    .movies(rs.getInt("movies"))
                    .build();

            res.add(dto);
        });

        return res;
    }

}
