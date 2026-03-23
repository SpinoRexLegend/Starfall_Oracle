package com.starfall.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class MLAnalysisService {

    private final WebClient mlWebClient;

    // The Service receives the 'Gate' (WebClient) from the Config
    public MLAnalysisService(WebClient mlWebClient) {
        this.mlWebClient = mlWebClient;
    }

    /**
     * This method handles the actual communication logic.
     * Input: String[] (e.g., Farm IDs or sensor metrics)
     * Output: List<String[]> (e.g., Multiple prediction sets)
     */
    public Mono<List<String[]>> getComplexPredictions(String[] inputData) {
        return mlWebClient.post()
                .uri("/ml/analyze")
                .bodyValue(inputData)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String[]>>() {})
                // You can add logic here to "clean" the data before sending it to the Controller
                .map(response -> {
                    // Example: Filter out empty results or log the data
                    return response;
                });
    }
}
