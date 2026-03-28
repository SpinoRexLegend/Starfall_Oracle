package com.starfall.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class MLAnalysisService {

    private final RestTemplate restTemplate;

    @Value("${python.service.url:http://localhost:5000}")
    private String pythonBaseUrl;

    public MLAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public HashMap<String, Object> callPython(HashMap<String, Object> inputMap) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(inputMap, headers);

        ResponseEntity<HashMap<String, Object>> response = restTemplate.exchange(
                pythonBaseUrl + "/predict",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<HashMap<String, Object>>() {}
        );

        return response.getBody();
    }
}