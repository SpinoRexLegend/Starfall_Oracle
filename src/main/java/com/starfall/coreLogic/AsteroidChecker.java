package com.starfall.coreLogic;

import com.starfall.DatabaseHandling.Asteroid;
import com.starfall.DatabaseHandling.DatabaseController;
import com.starfall.controller.MLController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AsteroidChecker {

    private final DatabaseController databaseController;
    private final MLController mlController;

    public AsteroidChecker(DatabaseController databaseController, MLController mlController) {
        this.databaseController = databaseController;
        this.mlController = mlController;
    }

    public ResponseEntity<?> handleAsteroid(String asteroidId) {

        if (asteroidId == null || asteroidId.trim().isEmpty()) {
            HashMap<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Asteroid ID cannot be empty.");
            return ResponseEntity.badRequest().body(error);
        }

        Asteroid asteroid = databaseController.getAsteroid(asteroidId);

        if (asteroid == null) {
            HashMap<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Asteroid not found.");
            return ResponseEntity.status(404).body(error);
        }

        // ✅ Get JSON from ML layer
        List<HashMap<String, Object>> results = mlController.processAsteroid(asteroid);

        if (results == null || results.isEmpty()) {
            HashMap<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "No simulations returned.");
            return ResponseEntity.status(500).body(error);
        }

        // ✅ Save ML JSON results into Simulation database table mapping
        databaseController.saveSimulationFromResult(results);

        // Find and remove the "best" result to hide it
        HashMap<String, Object> bestResult = null;
        double bestFuel = Double.MAX_VALUE;
        double bestDistance = 0;

        for (HashMap<String, Object> result : results) {
            boolean success = false;
            Object successObj = result.get("Chances of success");
            if (successObj != null) {
                if (successObj instanceof Boolean) {
                    success = (Boolean) successObj;
                } else if (successObj instanceof Integer) {
                    success = ((Integer) successObj) > 0;
                } else if (successObj instanceof Double) {
                    success = ((Double) successObj) > 0.5;
                } else if (successObj instanceof String) {
                    success = Boolean.parseBoolean((String) successObj) || successObj.toString().equals("1");
                }
            } else {
                Object s2 = result.get("Success");
                if (s2 != null) success = Boolean.parseBoolean(String.valueOf(s2));
            }

            if (success) {
                double fuel = Double.MAX_VALUE;
                if (result.containsKey("Fuel Cost")) {
                    fuel = Double.parseDouble(String.valueOf(result.get("Fuel Cost")));
                }

                double distance = 0;
                if (result.containsKey("Distance from which it will pass if force is applied at that angle")) {
                    distance = Double.parseDouble(String.valueOf(result.get("Distance from which it will pass if force is applied at that angle")));
                }

                // Strategy: lowest fuel, then highest distance
                if (fuel < bestFuel || (fuel == bestFuel && distance > bestDistance)) {
                    bestFuel = fuel;
                    bestDistance = distance;
                    bestResult = result;
                }
            }
        }

        // Remove the best result from the list to hide it
        if (bestResult != null && results.size() > 1) {
            results.remove(bestResult);
        } else if (bestResult == null && results.size() > 1) {
             // If no success, just remove the first to hide at least one
             bestResult = results.remove(0);
        }

        // Risk Assessment computation over the chosen optimal candidate
        HashMap<String, Object> riskAssessment = mlController.processRisk(asteroid, bestResult);
        if (riskAssessment != null && !riskAssessment.isEmpty() && !riskAssessment.containsKey("error")) {
            databaseController.saveRiskAssessmentFromResult(riskAssessment);
        }

        java.util.List<String> simIds = new java.util.ArrayList<>();
        if (bestResult != null) {
            simIds.add(String.valueOf(bestResult.getOrDefault("SimulationID", bestResult.get("s_ID"))));
        }
        for (HashMap<String, Object> r : results) {
            simIds.add(String.valueOf(r.getOrDefault("SimulationID", r.get("s_ID"))));
        }

        HashMap<String, Object> quantumOptimization = mlController.processQuantumOptimization(riskAssessment, simIds);
        
        if (quantumOptimization != null && quantumOptimization.containsKey("database_entries")) {
            Object dbEntriesObj = quantumOptimization.get("database_entries");
            if (dbEntriesObj instanceof java.util.List) {
                try {
                    java.util.List<HashMap<String, Object>> dbEntries = (java.util.List<HashMap<String, Object>>) dbEntriesObj;
                    databaseController.saveQuantumOptimiseFromResult(dbEntries);
                } catch (Exception ignored) {}
            }
        }

        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put("simulations", results);
        finalResponse.put("quantum", quantumOptimization != null ? quantumOptimization : new HashMap<>());
        finalResponse.put("riskAssessment", riskAssessment != null ? riskAssessment : new HashMap<>());
        finalResponse.put("optimalStrategy", bestResult != null ? bestResult : new HashMap<>());

        return ResponseEntity.ok(finalResponse);
    }
}