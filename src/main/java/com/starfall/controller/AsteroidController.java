package com.starfall.controller;

import com.starfall.coreLogic.AsteroidChecker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AsteroidController {

    private final AsteroidChecker asteroidChecker;

    public AsteroidController(AsteroidChecker asteroidChecker) {
        this.asteroidChecker = asteroidChecker;
    }

    @PostMapping("/asteroid")
    public ResponseEntity<?> receiveAsteroid(@RequestBody HashMap<String, String> payload) {

        String asteroidId = payload.get("asteroidId");

        // Delegates and RETURNS whatever comes back
        return asteroidChecker.handleAsteroid(asteroidId);
    }
}