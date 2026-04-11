package com.starfall.ProcessBuilder;

import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class PythonProcessBuilder {

    public HashMap<String, Object> buildRequest(String material, double diameter, double velocity) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("Material", material);
        map.put("Diameter", diameter);
        map.put("Velocity", velocity);

        return map;
    }

    public HashMap<String, Object> buildRiskRequest(String asteroidId, String material, double diameter, double velocity, double bestFuel, double bestDistance) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("asteroidID", asteroidId);
        map.put("Material", material);
        map.put("Diameter", diameter);
        map.put("Velocity", velocity);
        map.put("BestFuel", bestFuel);
        map.put("BestDistance", bestDistance);
        return map;
    }

    public HashMap<String, Object> buildQuantumRequest(double riskScore, boolean requiresDeflection, List<String> simulationIds) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("riskScore", riskScore);
        map.put("requiresDeflection", requiresDeflection);
        map.put("simulationIDs", simulationIds);
        return map;
    }
}