
package com.erriva.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a structured explanation of a Java error.
 *
 * An ErrorAnalysis object is immutable after creation.
 */
public final class ErrorAnalysis {

    private final String originalMessage;
    private final ErrorCategory category;
    private final String title;
    private final String explanation;
    private final List<String> possibleCauses;
    private final List<String> debuggingSteps;
    private final String incorrectExample;
    private final String correctedExample;

    public ErrorAnalysis(
            String originalMessage,
            ErrorCategory category,
            String title,
            String explanation,
            List<String> possibleCauses,
            List<String> debuggingSteps,
            String incorrectExample,
            String correctedExample) {

        this.originalMessage = requireText(
                originalMessage, "Original message");

        this.category = Objects.requireNonNull(
                category, "Category cannot be null");

        this.title = requireText(title, "Title");

        this.explanation = requireText(
                explanation, "Explanation");

        this.possibleCauses = copyTextList(
                possibleCauses, "Possible causes");

        this.debuggingSteps = copyTextList(
                debuggingSteps, "Debugging steps");

        this.incorrectExample = Objects.requireNonNull(
                incorrectExample,
                "Incorrect example cannot be null");

        this.correctedExample = Objects.requireNonNull(
                correctedExample,
                "Corrected example cannot be null");
    }

    private static String requireText(
            String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty");
        }

        return value;
    }

    private static List<String> copyTextList(
            List<String> values, String fieldName) {

        Objects.requireNonNull(
                values, fieldName + " cannot be null");

        for (String value : values) {
            requireText(value, fieldName + " entry");
        }

        return List.copyOf(values);
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<String> getPossibleCauses() {
        return possibleCauses;
    }

    public List<String> getDebuggingSteps() {
        return debuggingSteps;
    }

    public String getIncorrectExample() {
        return incorrectExample;
    }

    public String getCorrectedExample() {
        return correctedExample;
    }
}
