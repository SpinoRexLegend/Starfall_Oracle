package com.starfall.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/compute")
public class MLController {

    private final WebClient mlWebClient;

    public MLController(WebClient mlWebClient) {
        this.mlWebClient = mlWebClient;
    }

    @PostMapping("/process-data")
    public Mono<List<String[]>> processData(@RequestBody String[] inputData) {
        return mlWebClient.post()
                .uri("/ml/analyze")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String[]>>() {});
    }
}