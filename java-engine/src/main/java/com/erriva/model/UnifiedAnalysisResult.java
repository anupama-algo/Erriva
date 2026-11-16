
package com.erriva.model;

/**
 * Combines an error analysis with its extracted context.
 *
 * This class is immutable and provides one structured result
 * for the unified Java error analysis service.
 */
public final class UnifiedAnalysisResult {

    private final ErrorAnalysis analysis;
    private final ErrorContext context;

    /**
     * Creates a unified result.
     *
     * @param analysis the error explanation and debugging guidance
     * @param context the extracted exception and stack trace details
     */
    public UnifiedAnalysisResult(
            ErrorAnalysis analysis,
            ErrorContext context) {

        if (analysis == null) {
            throw new IllegalArgumentException(
                    "Error analysis cannot be null."
            );
        }

        if (context == null) {
            throw new IllegalArgumentException(
                    "Error context cannot be null."
            );
        }

        this.analysis = analysis;
        this.context = context;
    }

    public ErrorAnalysis getAnalysis() {
        return analysis;
    }

    public ErrorContext getContext() {
        return context;
    }
}
