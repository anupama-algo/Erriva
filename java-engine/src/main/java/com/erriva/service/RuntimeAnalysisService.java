
package com.erriva.service;

import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.knowledge.RuntimeErrorKnowledge;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;
import com.erriva.recognizer.RuntimeErrorRecognizer;

import java.util.List;

public final class RuntimeAnalysisService {

    private final RuntimeErrorRecognizer recognizer;

    public RuntimeAnalysisService() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        RuntimeErrorKnowledge.registerAll(repository);

        this.recognizer = new RuntimeErrorRecognizer(repository);
    }

    public boolean supports(String errorMessage) {
        return recognizer.supports(errorMessage);
    }

    public ErrorAnalysis analyze(String errorMessage) {

        if (errorMessage == null || errorMessage.isBlank()) {
            return new ErrorAnalysis(
                    errorMessage == null || errorMessage.isBlank()
                    ? "No error message provided"
                    : errorMessage,
                    ErrorCategory.UNKNOWN,
                    "Invalid Error Message",
                    "No Java error message was provided.",
                    List.of("The input is empty or contains only spaces."),
                    List.of("Paste a complete Java error message and try again."),
                    "",
                    ""
            );
        }

        if (!recognizer.supports(errorMessage)) {
            return new ErrorAnalysis(
                    errorMessage,
                    ErrorCategory.UNKNOWN,
                    "Unsupported Java Error",
                    "Erriva could not identify a supported Java runtime error in this message.",
                    List.of(
                            "The exception may not be supported yet.",
                            "The message may not contain a recognizable runtime exception."
                    ),
                    List.of(
                            "Check the exception name in the error message.",
                            "Include the complete exception message and stack trace.",
                            "Review the relevant line of Java code."
                    ),
                    "",
                    ""
            );
        }

        return recognizer.analyze(errorMessage);
    }
}
