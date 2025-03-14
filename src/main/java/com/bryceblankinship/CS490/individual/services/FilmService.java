package com.bryceblankinship.CS490.individual.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.bryceblankinship.CS490.individual.dto.ActorDTO;
import com.bryceblankinship.CS490.individual.dto.FilmDTO;

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

    public List<FilmDTO> searchFilms(String title, String actor, String category) {
        List<FilmDTO> res = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
    
        StringBuilder sql = new StringBuilder(
            "SELECT f.film_id, f.title, f.description, f.release_year, " +
            "f.length, f.rating, f.rental_rate, f.replacement_cost, c.name AS category, " +
            "COUNT(r.rental_id) AS rental_count, " +
            "GROUP_CONCAT(CONCAT(a.first_name, ' ', a.last_name) SEPARATOR ', ') AS actors " +
            "FROM film f " +
            "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
            "LEFT JOIN category c ON fc.category_id = c.category_id " +
            "LEFT JOIN film_actor fa ON f.film_id = fa.film_id " +
            "LEFT JOIN actor a ON fa.actor_id = a.actor_id " +
            "LEFT JOIN inventory i ON f.film_id = i.film_id " +
            "LEFT JOIN rental r ON i.inventory_id = r.inventory_id " +
            "WHERE 1=1"
        );
    
        if (title != null && !title.isEmpty()) {
            sql.append(" AND LOWER(f.title) LIKE LOWER(:title)");
            params.addValue("title", "%" + title + "%");
        }
    
        if (actor != null && !actor.isEmpty()) {
            sql.append(" AND (LOWER(a.first_name) LIKE LOWER(:actor) OR LOWER(a.last_name) LIKE LOWER(:actor))");
            params.addValue("actor", "%" + actor + "%");
        }
    
        if (category != null && !category.isEmpty()) {
            sql.append(" AND LOWER(c.name) = LOWER(:category)");
            params.addValue("category", category);
        }
    
        sql.append(" GROUP BY f.film_id, f.title, f.description, f.release_year, f.length, " +
                   "f.rating, f.rental_rate, f.replacement_cost, c.name");
    
        jdbc.query(sql.toString(), params, rs -> {
            String actorStr = rs.getString("actors");
            Set<String> actorList = new HashSet<>();
            if (actorStr != null && !actorStr.isEmpty()) {
                actorList = new HashSet<>(List.of(actorStr.split(",\\s*")));
            }
            FilmDTO dto = FilmDTO.builder()
                    .id(rs.getInt("film_id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .releaseYear(rs.getInt("release_year"))
                    .length(rs.getInt("length"))
                    .rating(rs.getString("rating"))
                    .rentalRate(rs.getDouble("rental_rate"))
                    .replacementCost(rs.getDouble("replacement_cost"))
                    .category(rs.getString("category"))
                    .rentalCount(rs.getInt("rental_count"))
                    .actors(actorList)
                    .build();
            res.add(dto);
        });
    
        return res;
    }
    
    public FilmDTO getFilmDetails(Integer filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);

        // First get the film details
        List<FilmDTO> res = new ArrayList<>();
        
        jdbc.query(
            "SELECT DISTINCT f.film_id, f.title, f.description, f.release_year, " +
            "f.length, f.rating, f.rental_rate, f.replacement_cost, c.name AS category, " +
            "COUNT(r.rental_id) AS rental_count " +
            "FROM film f " +
            "LEFT JOIN film_category fc ON f.film_id = fc.film_id " +
            "LEFT JOIN category c ON fc.category_id = c.category_id " +
            "LEFT JOIN inventory i ON f.film_id = i.film_id " +
            "LEFT JOIN rental r ON i.inventory_id = r.inventory_id " +
            "WHERE f.film_id = :filmId " +
            "GROUP BY f.film_id, f.title, f.description, f.release_year, " +
            "f.length, f.rating, f.rental_rate, f.replacement_cost, c.name",
            params,
            rs -> {
                FilmDTO dto = FilmDTO.builder()
                        .id(rs.getInt("film_id"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .releaseYear(rs.getInt("release_year"))
                        .length(rs.getInt("length"))
                        .rating(rs.getString("rating"))
                        .rentalRate(rs.getDouble("rental_rate"))
                        .replacementCost(rs.getDouble("replacement_cost"))
                        .category(rs.getString("category"))
                        .rentalCount(rs.getInt("rental_count"))
                        .build();
                res.add(dto);
            }
        );

        if (res.isEmpty()) {
            return null;
        }

        FilmDTO film = res.get(0);

        // Get the actors for this film with a separate query
        Set<String> actors = new HashSet<>();
        jdbc.query(
            "SELECT DISTINCT a.first_name, a.last_name " +
            "FROM actor a " +
            "INNER JOIN film_actor fa ON a.actor_id = fa.actor_id " +
            "WHERE fa.film_id = :filmId " +
            "ORDER BY a.first_name, a.last_name",
            params,
            rs -> {
                String actorName = rs.getString("first_name") + " " + rs.getString("last_name");
                actors.add(actorName);
            }
        );

        film.setActors(actors);
        return film;
    }

    public boolean rentFilm(Integer filmId, Integer customerId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("filmId", filmId);
        params.addValue("customerId", customerId);

        // Find available inventory
        Integer inventoryId = jdbc.query(
            "SELECT i.inventory_id FROM inventory i " +
            "LEFT JOIN rental r ON i.inventory_id = r.inventory_id AND r.return_date IS NULL " +
            "WHERE i.film_id = :filmId AND r.rental_id IS NULL " +
            "LIMIT 1",
            params,
            rs -> rs.next() ? rs.getInt("inventory_id") : null
        );

        if (inventoryId == null) {
            return false; // No available inventory
        }

        params.addValue("inventoryId", inventoryId);
        params.addValue("staffId", 1); // Using default staff ID for simplicity

        // Create new rental
        jdbc.update(
            "INSERT INTO rental (rental_date, inventory_id, customer_id, staff_id) " +
            "VALUES (NOW(), :inventoryId, :customerId, :staffId)",
            params
        );

        // Create payment record
        jdbc.update(
            "INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date) " +
            "SELECT :customerId, :staffId, r.rental_id, f.rental_rate, NOW() " +
            "FROM rental r " +
            "JOIN inventory i ON r.inventory_id = i.inventory_id " +
            "JOIN film f ON i.film_id = f.film_id " +
            "WHERE r.inventory_id = :inventoryId " +
            "ORDER BY r.rental_id DESC LIMIT 1",
            params
        );

        return true;
    }

    public ActorDTO getActorDetails(Integer actorId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("actorId", actorId);

        // Get actor basic info
        List<ActorDTO> res = new ArrayList<>();
        jdbc.query(
            "SELECT a.actor_id, a.first_name, a.last_name, COUNT(DISTINCT f.film_id) as movies " +
            "FROM actor a " +
            "JOIN film_actor fa ON a.actor_id = fa.actor_id " +
            "JOIN film f ON fa.film_id = f.film_id " +
            "WHERE a.actor_id = :actorId " +
            "GROUP BY a.actor_id, a.first_name, a.last_name",
            params,
            rs -> {
                ActorDTO dto = ActorDTO.builder()
                    .id(rs.getInt("actor_id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .movies(rs.getInt("movies"))
                    .build();
                res.add(dto);
            }
        );

        if (res.isEmpty()) {
            return null;
        }

        ActorDTO actor = res.get(0);

        // Get actor's top 5 rented films
        List<FilmDTO> topFilms = new ArrayList<>();
        jdbc.query(
            "SELECT f.film_id, f.title, c.name as category, COUNT(r.rental_id) as rental_count " +
            "FROM film f " +
            "JOIN film_actor fa ON f.film_id = fa.film_id " +
            "JOIN film_category fc ON f.film_id = fc.film_id " +
            "JOIN category c ON fc.category_id = c.category_id " +
            "JOIN inventory i ON f.film_id = i.film_id " +
            "JOIN rental r ON i.inventory_id = r.inventory_id " +
            "WHERE fa.actor_id = :actorId " +
            "GROUP BY f.film_id, f.title, c.name " +
            "ORDER BY rental_count DESC " +
            "LIMIT 5",
            params,
            rs -> {
                FilmDTO film = FilmDTO.builder()
                    .id(rs.getInt("film_id"))
                    .title(rs.getString("title"))
                    .category(rs.getString("category"))
                    .rentalCount(rs.getInt("rental_count"))
                    .build();
                topFilms.add(film);
            }
        );

        actor.setTopFilms(topFilms);
        return actor;
    }
}
