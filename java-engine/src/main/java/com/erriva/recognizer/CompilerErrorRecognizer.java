
package com.erriva.recognizer;

import com.erriva.knowledge.ErrorKnowledge;
import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.model.ErrorAnalysis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class CompilerErrorRecognizer implements ErrorRecognizer {

    private final ErrorKnowledgeRepository repository;
    private final Map<Pattern, String> recognitionRules;

    public CompilerErrorRecognizer(ErrorKnowledgeRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "Repository cannot be null");

        this.recognitionRules = new LinkedHashMap<>();

        addRule("cannot find symbol",
                "COMPILER_CANNOT_FIND_SYMBOL");

        addRule("incompatible types:",
                "COMPILER_INCOMPATIBLE_TYPES");

        addRule("';' expected",
                "COMPILER_SEMICOLON_EXPECTED");

        addRule("missing return statement",
                "COMPILER_MISSING_RETURN");

        addRule("variable\\s+\\S+\\s+might not have been initialized",
                "COMPILER_UNINITIALIZED_VARIABLE");

        addRule("illegal start of expression",
                "COMPILER_ILLEGAL_START");

        addRule("reached end of file while parsing",
                "COMPILER_REACHED_END_OF_FILE");

        addRule("non-static (?:variable|method)\\s+.+?\\s+cannot be referenced from a static context",
                "COMPILER_NON_STATIC_REFERENCE");

    }

    private void addRule(String diagnostic, String knowledgeId) {

        String expression =
                "(?m)^\\s*(?:(?:[^\\r\\n]*\\.java:\\d+:\\s*)?"
                + "error:\\s*)?"
                + diagnostic
                + "(?=\\s|$)";

        recognitionRules.put(
                Pattern.compile(expression),
                knowledgeId
        );
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
                    "Unsupported or invalid compiler error message");
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
