package com.bryceblankinship.CS490.individual.controllers;

import com.bryceblankinship.CS490.individual.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    private ResponseEntity<?> getTopFilms(){
        return ResponseEntity.ok(filmService.getTopFilms());
    }

    @GetMapping("/actors")
    private ResponseEntity<?> getTopActors(){
        return ResponseEntity.ok(filmService.getTopActors());
    }

}
