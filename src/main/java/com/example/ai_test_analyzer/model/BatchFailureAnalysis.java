
package com.example.ai_test_analyzer.model;

import java.util.List;

public class BatchFailureAnalysis {

    private int totalFailures;

    private String overallAssessment;

    private List<String> commonPatterns;

    private List<String> recommendedActions;

    public int getTotalFailures() {
        return totalFailures;
    }

    public void setTotalFailures(int totalFailures) {
        this.totalFailures = totalFailures;
    }

    public String getOverallAssessment() {
        return overallAssessment;
    }

    public void setOverallAssessment(String overallAssessment) {
        this.overallAssessment = overallAssessment;
    }

    public List<String> getCommonPatterns() {
        return commonPatterns;
    }

    public void setCommonPatterns(List<String> commonPatterns) {
        this.commonPatterns = commonPatterns;
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(List<String> recommendedActions) {
        this.recommendedActions = recommendedActions;
    }
}