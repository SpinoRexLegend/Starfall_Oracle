package com.starfall.service;

import com.starfall.ProcessBuilder.PythonProcessBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class MLAnalysisService {

    private final RestTemplate restTemplate;
    private final PythonProcessBuilder processBuilder;

    @Value("${python.service.url:http://localhost:5000}")
    private String pythonBaseUrl;

    public MLAnalysisService(RestTemplate restTemplate, PythonProcessBuilder processBuilder) {
        this.restTemplate = restTemplate;
        this.processBuilder = processBuilder;
    }

    public List<HashMap<String, Object>> callPython(String material, double dia, double vel) {

        HashMap<String, Object> requestBody = processBuilder.buildRequest(material, dia, vel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> requestEntity =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<HashMap<String, Object>> response =
                    restTemplate.exchange(
                            pythonBaseUrl + "/predict",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<HashMap<String, Object>>() {}
                    );

            HashMap<String, Object> body = response.getBody();
            if (body != null && body.containsKey("output")) {
                return (List<HashMap<String, Object>>) body.get("output");
            }
            return new ArrayList<>();

        } catch (Exception e) {

            HashMap<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Python service unavailable");
            errorResponse.put("details", e.getMessage());

            List<HashMap<String, Object>> errList = new ArrayList<>();
            errList.add(errorResponse);
            return errList;
        }
    }

    public HashMap<String, Object> callRiskAssessmentPython(String asteroidId, String material, double dia, double vel, double bestFuel, double bestDistance) {
        HashMap<String, Object> requestBody = processBuilder.buildRiskRequest(asteroidId, material, dia, vel, bestFuel, bestDistance);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> requestEntity =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<HashMap<String, Object>> response =
                    restTemplate.exchange(
                            pythonBaseUrl + "/assess",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<HashMap<String, Object>>() {}
                    );

            HashMap<String, Object> body = response.getBody();
            if (body != null && body.containsKey("output")) {
                return (HashMap<String, Object>) body.get("output");
            }
            return new HashMap<>();

        } catch (Exception e) {
            HashMap<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Python service unavailable for risk assessment");
            errorResponse.put("details", e.getMessage());
            return errorResponse;
        }
    }

    public HashMap<String, Object> callQuantumPython(double riskScore, boolean requiresDeflection, List<String> simulationIds) {
        HashMap<String, Object> requestBody = processBuilder.buildQuantumRequest(riskScore, requiresDeflection, simulationIds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> requestEntity =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<HashMap<String, Object>> response =
                    restTemplate.exchange(
                            pythonBaseUrl + "/quantum",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<HashMap<String, Object>>() {}
                    );

            HashMap<String, Object> body = response.getBody();
            if (body != null && body.containsKey("output")) {
                return (HashMap<String, Object>) body.get("output");
            }
            return new HashMap<>();

        } catch (Exception e) {
            HashMap<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Python service unavailable for quantum optimisation");
            errorResponse.put("details", e.getMessage());
            return errorResponse;
        }
    }
}