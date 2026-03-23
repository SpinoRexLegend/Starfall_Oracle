package com.starfall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfig {
    @Bean
    public WebClient pythonWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:5000") // Python Server URL
                .build();
    }
}