
package com.example.ai_test_analyzer.service;

import com.example.ai_test_analyzer.client.OllamaClient;
import com.example.ai_test_analyzer.model.TestFailure;
import com.example.ai_test_analyzer.model.FailureAnalysis;
import org.springframework.stereotype.Service;

@Service
public class FailureAnalysisService {

    private final OllamaClient ollamaClient;

    public FailureAnalysisService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public FailureAnalysis analyze(TestFailure failure) {

        String prompt = """
                You are an experienced software test automation engineer.

                Analyze the following API test failure.

                Test Name: %s
                HTTP Status Code: %d
                Error Message: %s

                Explain the most likely reason for this failure.
                """.formatted(
                failure.getTestName(),
                failure.getStatusCode(),
                failure.getErrorMessage()
        );

        String aiResponse = ollamaClient.generate(prompt);

        System.out.println("AI Analysis: " + aiResponse);

        return new FailureAnalysis();
    }
}
