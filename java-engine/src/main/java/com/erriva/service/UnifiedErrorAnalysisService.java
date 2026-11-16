
package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorContext;
import com.erriva.model.UnifiedAnalysisResult;

/**
 * Coordinates Java error recognition and context extraction.
 *
 * This service provides one entry point for analyzing Java
 * compiler errors, runtime errors, and unsupported messages.
 */
public final class UnifiedErrorAnalysisService {

    private final CompilerAnalysisService compilerService;
    private final RuntimeAnalysisService runtimeService;
    private final ContextExtractionService contextService;

    public UnifiedErrorAnalysisService() {
        this.compilerService = new CompilerAnalysisService();
        this.runtimeService = new RuntimeAnalysisService();
        this.contextService = new ContextExtractionService();
    }

    /**
     * Analyzes a Java error message and extracts its context.
     *
     * @param errorMessage the Java error message
     * @return a combined error analysis and context
     */
    public UnifiedAnalysisResult analyze(String errorMessage) {

        ErrorAnalysis analysis;

        // Handle null, empty, and whitespace-only input first.
        if (errorMessage == null || errorMessage.isBlank()) {
            analysis = runtimeService.analyze(errorMessage);

        } else if (compilerService.supports(errorMessage)) {
            // Compiler recognition has first priority.
            analysis = compilerService.analyze(errorMessage);

        } else if (runtimeService.supports(errorMessage)) {
            // Runtime recognition has second priority.
            analysis = runtimeService.analyze(errorMessage);

        } else {
            // Reuse the existing UNKNOWN fallback.
            analysis = runtimeService.analyze(errorMessage);
        }

        // Extract context independently of the analysis category.
        ErrorContext context = contextService.extract(errorMessage);

        return new UnifiedAnalysisResult(analysis, context);
    }
}
