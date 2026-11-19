
package com.example.ai_test_analyzer.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class OllamaClient {

    private final RestClient restClient;

    public OllamaClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generate(String prompt) {

        Map<String, Object> request = new HashMap<>();

        request.put("model", "qwen2.5:3b");
        request.put("prompt", prompt);
        request.put("stream", false);
        request.put("format", "json");

        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.1);
        options.put("num_ctx", 4096);
        options.put("num_predict", 300);

        request.put("options", options);

        Map response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        return (String) response.get("response");
    }
}