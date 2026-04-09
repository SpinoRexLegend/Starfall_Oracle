package com.starfall.controller;

import com.starfall.DatabaseHandling.DatabaseController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AsteroidController {

    @PostMapping("/asteroid")
    public Map<String, String> receiveAsteroid(@RequestBody Map<String, String> payload) {

        String asteroidId = payload.get("asteroidId");
        DatabaseController dbms = new DatabaseController();

        System.out.println("Received Asteroid ID: " + asteroidId);

        dbms.getAsteroid(asteroidId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "received");
        response.put("asteroidId", asteroidId);

        return response;
    }
}