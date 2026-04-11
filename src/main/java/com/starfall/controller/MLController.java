package com.starfall.controller;

import com.starfall.DatabaseHandling.Asteroid;
import com.starfall.service.MLAnalysisService;

import org.springframework.stereotype.Controller;

import java.util.HashMap;

import java.util.List;

@Controller
public class MLController {

    private final MLAnalysisService mlAnalysisService;

    public MLController(MLAnalysisService mlAnalysisService) {
        this.mlAnalysisService = mlAnalysisService;
    }

    public List<HashMap<String, Object>> processAsteroid(Asteroid asteroid) {

        String material = asteroid.getComposition();
        double dia = asteroid.getDiameter();
        double vel = asteroid.getVelocity();

        return mlAnalysisService.callPython(material, dia, vel);
    }

    public HashMap<String, Object> processRisk(Asteroid asteroid, HashMap<String, Object> bestSimulation) {
        String material = asteroid.getComposition();
        double dia = asteroid.getDiameter();
        double vel = asteroid.getVelocity();

        double bestFuel = bestSimulation != null && bestSimulation.containsKey("Fuel Cost")
                ? Double.parseDouble(String.valueOf(bestSimulation.get("Fuel Cost")))
                : 0;
        double bestDist = bestSimulation != null
                && bestSimulation.containsKey("Distance from which it will pass if force is applied at that angle")
                        ? Double.parseDouble(String.valueOf(bestSimulation
                                .get("Distance from which it will pass if force is applied at that angle")))
                        : 0;

        return mlAnalysisService.callRiskAssessmentPython(asteroid.getAsteroid_Id(), material, dia, vel, bestFuel,
                bestDist);
    }

    public HashMap<String, Object> processQuantumOptimization(HashMap<String, Object> riskAssessment, List<String> simulationIds) {
        double riskScore = riskAssessment != null && riskAssessment.containsKey("riskScore") ? Double.parseDouble(String.valueOf(riskAssessment.get("riskScore"))) : 0;
        boolean requiresDeflection = riskAssessment != null && riskAssessment.containsKey("requiresDeflection") && Boolean.parseBoolean(String.valueOf(riskAssessment.get("requiresDeflection")));

        return mlAnalysisService.callQuantumPython(riskScore, requiresDeflection, simulationIds);
    }
}