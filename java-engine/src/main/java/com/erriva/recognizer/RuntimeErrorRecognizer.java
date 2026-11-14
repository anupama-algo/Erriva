
package com.erriva.recognizer;

import com.erriva.knowledge.ErrorKnowledge;
import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.model.ErrorAnalysis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class RuntimeErrorRecognizer implements ErrorRecognizer {

    private final ErrorKnowledgeRepository repository;
    private final Map<Pattern, String> recognitionRules;

    public RuntimeErrorRecognizer(ErrorKnowledgeRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "Repository cannot be null");

        this.recognitionRules = new LinkedHashMap<>();

        addRule("NullPointerException", "RUNTIME_NULL_POINTER");
        addRule("ArrayIndexOutOfBoundsException", "RUNTIME_ARRAY_INDEX");
        addRule("ArithmeticException", "RUNTIME_ARITHMETIC");
        addRule("NumberFormatException", "RUNTIME_NUMBER_FORMAT");
        addRule("ClassCastException", "RUNTIME_CLASS_CAST");
    }

    private void addRule(String exceptionName, String knowledgeId) {
        String expression =
                "(?m)^\\s*(?:Exception in thread \"[^\"]+\"\\s+)?"
                + "(?:java\\.lang\\.)?"
                + Pattern.quote(exceptionName)
                + "(?=\\s|:|$)";

        recognitionRules.put(Pattern.compile(expression), knowledgeId);
    }

    private String findKnowledgeId(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return null;
        }

        for (Map.Entry<Pattern, String> rule : recognitionRules.entrySet()) {
            if (rule.getKey().matcher(errorMessage).find()) {
                return rule.getValue();
            }
        }

        return null;
    }

    @Override
    public boolean supports(String errorMessage) {
        return findKnowledgeId(errorMessage) != null;
    }

    @Override
    public ErrorAnalysis analyze(String errorMessage) {
        String knowledgeId = findKnowledgeId(errorMessage);

        if (knowledgeId == null) {
            throw new IllegalArgumentException(
                    "Unsupported or invalid runtime error message");
        }

        ErrorKnowledge knowledge = repository.findById(knowledgeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing error knowledge: " + knowledgeId));

        return new ErrorAnalysis(
                errorMessage,
                knowledge.getCategory(),
                knowledge.getTitle(),
                knowledge.getExplanation(),
                knowledge.getPossibleCauses(),
                knowledge.getDebuggingSteps(),
                knowledge.getIncorrectExample(),
                knowledge.getCorrectedExample()
        );
    }
}
