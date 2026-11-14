
package com.erriva.service;

import com.erriva.knowledge.CompilerErrorKnowledge;
import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;
import com.erriva.recognizer.CompilerErrorRecognizer;

import java.util.Collections;

public final class CompilerAnalysisService {

    private final CompilerErrorRecognizer recognizer;

    public CompilerAnalysisService() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        CompilerErrorKnowledge.registerAll(repository);

        this.recognizer =
                new CompilerErrorRecognizer(repository);
    }

    public boolean supports(String errorMessage) {
        return recognizer.supports(errorMessage);
    }

    public ErrorAnalysis analyze(String errorMessage) {

        if (recognizer.supports(errorMessage)) {
            return recognizer.analyze(errorMessage);
        }

        String originalMessage =
                errorMessage == null || errorMessage.isBlank()
                        ? "No error message provided"
                        : errorMessage;

        return new ErrorAnalysis(
                originalMessage,
                ErrorCategory.UNKNOWN,
                "Unrecognized Compiler Error",
                "Erriva could not identify this compiler error. "
                        + "Check the complete compiler output and "
                        + "verify the reported source code location.",
                Collections.singletonList(
                        "The compiler diagnostic is not currently "
                                + "supported by Erriva."
                ),
                Collections.singletonList(
                        "Copy the complete compiler error message "
                                + "and check the indicated line."
                ),
                "",
                ""
        );
    }
}
