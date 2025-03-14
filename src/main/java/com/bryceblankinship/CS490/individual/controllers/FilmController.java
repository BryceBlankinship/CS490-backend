package com.bryceblankinship.CS490.individual.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bryceblankinship.CS490.individual.dto.ActorDTO;
import com.bryceblankinship.CS490.individual.dto.FilmDTO;
import com.bryceblankinship.CS490.individual.services.FilmService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    private ResponseEntity<?> getTopFilms() {
        return ResponseEntity.ok(filmService.getTopFilms());
    }

    @GetMapping("/actors")
    private ResponseEntity<?> getTopActors() {
        return ResponseEntity.ok(filmService.getTopActors());
    }

    @GetMapping("/films/search")
    private ResponseEntity<?> searchFilms(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(filmService.searchFilms(title, actor, category));
    }

    @GetMapping("/films/{id}")
    private ResponseEntity<?> getFilmDetails(@PathVariable("id") Integer filmId) {
        FilmDTO film = filmService.getFilmDetails(filmId);
        if (film == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(film);
    }

    @PostMapping("/films/{id}/rent")
    private ResponseEntity<?> rentFilm(
            @PathVariable("id") Integer filmId,
            @RequestParam Integer customerId) {
        boolean success = filmService.rentFilm(filmId, customerId);
        if (!success) {
            return ResponseEntity.badRequest().body("Film not available for rent");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/actors/{id}/details")
    private ResponseEntity<?> getActorDetails(@PathVariable("id") Integer actorId) {
        ActorDTO actor = filmService.getActorDetails(actorId);
        if (actor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actor);
    }
}
