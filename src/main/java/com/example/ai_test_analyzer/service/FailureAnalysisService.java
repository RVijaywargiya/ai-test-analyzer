
package com.example.ai_test_analyzer.service;

import com.example.ai_test_analyzer.client.OllamaClient;
import com.example.ai_test_analyzer.model.BatchFailureAnalysis;
import com.example.ai_test_analyzer.model.TestFailure;
import com.example.ai_test_analyzer.model.FailureAnalysis;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FailureAnalysisService {

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;
    private final FailureCorrelationService correlationService;

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "APPLICATION",
            "BUSINESS_RULE",
            "TEST_DATA",
            "NETWORK",
            "AUTHENTICATION",
            "AUTOMATION",
            "UNKNOWN"
    );

    public FailureAnalysisService(OllamaClient ollamaClient, ObjectMapper objectMapper, FailureCorrelationService correlationService) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = objectMapper;
        this.correlationService = correlationService;
    }

    public FailureAnalysis analyze(TestFailure failure) {

        String prompt = """
        You are an experienced software test automation engineer.

        Analyze the following API test failure.

        Test Name: %s
        HTTP Status Code: %d
        Error Message: %s
        
                STRICT OUTPUT RULES:
                
                1. Every requested JSON field is mandatory.
                2. Never return null for any field.
                3. Category must be exactly one of:
                   APPLICATION, BUSINESS_RULE, TEST_DATA,
                   NETWORK, AUTHENTICATION, AUTOMATION, UNKNOWN.
                4. Severity must be exactly one of:
                   HIGH, MEDIUM, LOW.
                5. If the category cannot be determined,
                   return UNKNOWN.
                6. Always include 3 to 5 investigation steps.
                7. Return valid JSON only.

        Return ONLY a valid JSON object with these exact fields:

        {
                
        Classify the failure using the following rules:

        1. APPLICATION:
           Unexpected application exceptions, server defects,
           or application processing errors.

        2. BUSINESS_RULE:
           The application rejects an operation because a
           business condition or workflow rule is not satisfied.

        3. TEST_DATA:
           Invalid, missing, inconsistent, or incorrectly
           prepared test data causes the failure.

        4. NETWORK:
           Connection failures, timeouts, DNS issues,
           or communication problems.

        5. AUTHENTICATION:
           Invalid credentials, expired tokens, or permission issues.

        6. AUTOMATION:
           Test script defects, incorrect assertions,
           locator failures, or automation configuration problems.

        7. UNKNOWN:
           Insufficient evidence to determine the category.

        Important classification guidelines:

        - Do not classify a failure as APPLICATION just because
          the HTTP status code is 500.
        - Analyze the actual error message and available context.
        - Distinguish a business-rule rejection from an application defect.
        - Do not assume test data is incorrect without evidence.
        - If evidence is insufficient, choose UNKNOWN.
        - Explain why you selected the category.
        - Generate 3 to 5 specific, practical investigation steps.

                  The steps must be relevant to the identified failure.
                  Avoid generic recommendations.
                  Do not claim that an unverified cause is confirmed.
                                                                                                ,
          "severity": "HIGH or MEDIUM or LOW",
          "probableCause": "Most likely reason for failure",
          "explanation": "Detailed explanation of the failure",
          "testName": "Original test name",
          "investigationSteps": [
                      "First investigation step",
                      "Second investigation step",
                      "Third investigation step"
                    ],
          "confidence": "HIGH, MEDIUM or LOW"
          Confidence guidelines:
                
                  HIGH:
                  The error message provides strong evidence for the classification.
                
                  MEDIUM:
                  The error message suggests a likely cause, but additional
                  investigation is needed.
                
                  LOW:
                  The available information is insufficient to identify
                  a reliable cause.          
        }

        Do not include markdown or additional text.
        """.formatted(
                failure.getTestName(),
                failure.getStatusCode(),
                failure.getErrorMessage()
        );

        String aiResponse = ollamaClient.generate(prompt);

        try {

            FailureAnalysis analysis =
                    objectMapper.readValue(aiResponse, FailureAnalysis.class);

            // Validate category
            if (analysis.getCategory() == null ||
                    !VALID_CATEGORIES.contains(analysis.getCategory())) {

                analysis.setCategory("UNKNOWN");
            }

            // Validate severity
            if (analysis.getSeverity() == null ||
                    !Set.of("HIGH", "MEDIUM", "LOW")
                            .contains(analysis.getSeverity())) {

                analysis.setSeverity("MEDIUM");
            }

            // Validate test name
            if (analysis.getTestName() == null ||
                    analysis.getTestName().isBlank()) {

                analysis.setTestName(failure.getTestName());
            }

            // Validate investigation steps
            if (analysis.getInvestigationSteps() == null ||
                    analysis.getInvestigationSteps().isEmpty()) {

                analysis.setInvestigationSteps(
                        List.of("Review application logs and investigate the failure.")
                );
            }

            return analysis;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse AI response: " + aiResponse, e
            );
        }
    }


    public BatchFailureAnalysis analyzeBatch(List<TestFailure> failures) {

        StringBuilder failureDetails = new StringBuilder();

        for (TestFailure failure : failures) {

            failureDetails.append("""
                
                Test Name: %s
                HTTP Status: %d
                Error Message: %s
                
                """.formatted(
                    failure.getTestName(),
                    failure.getStatusCode(),
                    failure.getErrorMessage()
            ));
        }

        String prompt = """
            You are an experienced software test automation engineer.

            Analyze the following API test failures together.

            Identify:
            1. Common failure patterns.
            2. Failures that may share an underlying cause.
            3. Whether the failures indicate a possible common service issue.
            4. Recommended investigation actions.

            Do not assume that failures are related merely because
            they occurred in the same execution.

            Base your conclusions only on the supplied evidence.

            Test failures:
            %s

            Return ONLY valid JSON with these fields:

            {
              "totalFailures": 0,
              "overallAssessment": "Overall assessment",
              "commonPatterns": [
                "Identified pattern"
              ],
              "recommendedActions": [
                "Recommended investigation action"
              ]
            }
            """.formatted(failureDetails);

        String aiResponse = ollamaClient.generate(prompt);

        try {
            BatchFailureAnalysis analysis =
                    objectMapper.readValue(
                            aiResponse,
                            BatchFailureAnalysis.class
                    );

            analysis.setTotalFailures(failures.size());

            return analysis;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse batch analysis: " + aiResponse, e
            );
        }
    }

    public Map<String, List<TestFailure>> testCorrelation(
            List<TestFailure> failures) {

        return correlationService.correlate(failures);
    }

}
