
package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorContext;
import com.erriva.model.InputValidationResult;
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
    private final ErrorInputValidator inputValidator;

    public UnifiedErrorAnalysisService() {
        this.compilerService = new CompilerAnalysisService();
        this.runtimeService = new RuntimeAnalysisService();
        this.contextService = new ContextExtractionService();
        this.inputValidator = new ErrorInputValidator();
    }

    /**
     * Analyzes a Java error message and extracts its context.
     *
     * @param errorMessage the Java error message
     * @return a combined error analysis and context
     * @throws IllegalArgumentException if input exceeds the limit
     */
    public UnifiedAnalysisResult analyze(String errorMessage) {

        InputValidationResult validation =
                inputValidator.validate(errorMessage);

        // Preserve existing behavior for missing input.
        if (errorMessage == null || errorMessage.isBlank()) {
            ErrorAnalysis analysis =
                    runtimeService.analyze(errorMessage);

            ErrorContext context =
                    contextService.extract(errorMessage);

            return new UnifiedAnalysisResult(analysis, context);
        }

        // Reject oversized input before recognition or parsing.
        if (!validation.isValid()) {
            throw new IllegalArgumentException(
                    validation.getMessage()
            );
        }

        ErrorAnalysis analysis;

        if (compilerService.supports(errorMessage)) {
            analysis = compilerService.analyze(errorMessage);

        } else if (runtimeService.supports(errorMessage)) {
            analysis = runtimeService.analyze(errorMessage);

        } else {
            analysis = runtimeService.analyze(errorMessage);
        }

        ErrorContext context =
                contextService.extract(errorMessage);

        return new UnifiedAnalysisResult(analysis, context);
    }
}
