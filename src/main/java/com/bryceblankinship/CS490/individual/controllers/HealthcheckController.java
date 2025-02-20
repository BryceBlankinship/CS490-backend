package com.bryceblankinship.CS490.individual.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {

    @GetMapping("/healthcheck")
    private ResponseEntity<String> healthcheck(){
        return ResponseEntity.ok("Hello World");
    }

}
