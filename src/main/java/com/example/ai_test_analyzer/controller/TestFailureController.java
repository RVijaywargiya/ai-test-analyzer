package com.example.ai_test_analyzer.controller;

import com.example.ai_test_analyzer.client.OllamaClient;
import com.example.ai_test_analyzer.model.BatchFailureAnalysis;
import com.example.ai_test_analyzer.model.FailureAnalysis;
//import com.example.testanalyzer.model.FailureAnalysis;
import com.example.ai_test_analyzer.model.TestFailure;
import com.example.ai_test_analyzer.service.FailureAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/failures")
public class TestFailureController {

    private final FailureAnalysisService analysisService;
    private final OllamaClient ollamaClient;

    public TestFailureController(
            FailureAnalysisService analysisService, OllamaClient ollamaClient) {
        this.analysisService = analysisService;
        this.ollamaClient = ollamaClient;
    }

    @PostMapping("/analyze")
    public FailureAnalysis analyze(
            @RequestBody TestFailure failure) {

        return analysisService.analyze(failure);
    }

    @GetMapping("/ai-test")
    public String testAI() {

        String prompt = """
            Explain why an API test might receive HTTP 500.
            Give a short answer.
            """;

        return ollamaClient.generate(prompt);
    }


    @PostMapping("/analyze-batch")
    public BatchFailureAnalysis analyzeBatch(
            @RequestBody List<TestFailure> failures) {

        return analysisService.analyzeBatch(failures);
    }

    @PostMapping("/correlate")
    public Map<String, List<TestFailure>> correlate(
            @RequestBody List<TestFailure> failures) {

        return analysisService.testCorrelation(failures);
    }

}
