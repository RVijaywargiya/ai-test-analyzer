
package com.example.ai_test_analyzer.model;

import java.util.List;

public class FailureAnalysis {

    private String testName;
    private String category;
    private String severity;
    private String probableCause;
    private String explanation;
    private List<String> investigationSteps;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(String probableCause) {
        this.probableCause = probableCause;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getInvestigationSteps() {
        return investigationSteps;
    }

    public void setInvestigationSteps(List<String> investigationSteps) {
        this.investigationSteps = investigationSteps;
    }
}
