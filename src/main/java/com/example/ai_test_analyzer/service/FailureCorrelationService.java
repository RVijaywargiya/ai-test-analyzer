
package com.example.ai_test_analyzer.service;

import com.example.ai_test_analyzer.model.TestFailure;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FailureCorrelationService {

    public Map<String, List<TestFailure>> correlate(
            List<TestFailure> failures) {

        Map<String, List<TestFailure>> groups = new LinkedHashMap<>();

        for (TestFailure failure : failures) {

            String error = failure.getErrorMessage()
                    .toLowerCase();

            String groupKey;

            if (error.contains("inventory service")) {
                groupKey = "INVENTORY_SERVICE";
            } else if (error.contains("timeout")) {
                groupKey = "TIMEOUT";
            } else if (error.contains("invalid")) {
                groupKey = "INVALID_DATA";
            } else if (error.contains("not found")) {
                groupKey = "RESOURCE_NOT_FOUND";
            } else {
                groupKey = "OTHER";
            }

            groups.computeIfAbsent(
                    groupKey,
                    key -> new ArrayList<>()
            ).add(failure);
        }

        return groups;
    }
}