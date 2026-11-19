package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorContext;
import com.erriva.model.UnifiedAnalysisResult;

import java.util.List;
import java.util.Objects;

public final class MarkdownReportService {

    public String generate(UnifiedAnalysisResult result) {

        Objects.requireNonNull(
                result, "Analysis result cannot be null"
        );

        ErrorAnalysis analysis = result.getAnalysis();
        ErrorContext context = result.getContext();

        StringBuilder report = new StringBuilder();

        report.append("# Erriva - Java Error Analysis Report\n\n");

        appendSection(report, "Error Title",
                analysis.getTitle());

        appendSection(report, "Category",
                analysis.getCategory().name());

        appendTextBlock(report, "Original Error",
                analysis.getOriginalMessage());

        appendSection(report, "Explanation",
                analysis.getExplanation());

        appendList(report, "Possible Causes",
                analysis.getPossibleCauses());

        appendList(report, "Debugging Steps",
                analysis.getDebuggingSteps());

        appendCode(report, "Incorrect Example",
                analysis.getIncorrectExample());

        appendCode(report, "Corrected Example",
                analysis.getCorrectedExample());

        if (context.hasException()) {
            appendSection(report, "Exception Type",
                    context.getExceptionType());

            String message = context.getExceptionMessage();

            if (message != null && !message.isBlank()) {
                appendSection(report, "Exception Message",
                        message);
            }
        }

        if (context.hasFrames()) {
                StringBuilder stackTrace = new StringBuilder();

            context.getFrames().forEach(frame -> {
                stackTrace.append("at ")
                        .append(frame.getClassName())
                        .append(".")
                        .append(frame.getMethodName())
                        .append("(");

                if (frame.getFileName() == null) {
                    stackTrace.append("Unknown Source");
                } else if (frame.hasSourceLocation()) {
                    stackTrace.append(frame.getFileName())
                            .append(":")
                            .append(frame.getLineNumber());
                } else {
                    stackTrace.append(frame.getFileName());
                }

                stackTrace.append(")\n");
            });

            appendTextBlock(
                    report,
                    "Stack Trace",
                    stackTrace.toString().stripTrailing()
            );
        }


        return report.toString();
    }

    private void appendSection(
            StringBuilder report,
            String heading,
            String content) {

        report.append("## ")
                .append(heading)
                .append("\n\n")
                .append(content)
                .append("\n\n");
    }

    private void appendList(
            StringBuilder report,
            String heading,
            List<String> items) {

        report.append("## ")
                .append(heading)
                .append("\n\n");

        if (items.isEmpty()) {
            report.append("Not available.\n\n");
            return;
        }

        for (String item : items) {
            report.append("- ")
                    .append(item)
                    .append("\n");
        }

        report.append("\n");
    }


    private void appendTextBlock(
            StringBuilder report,
            String heading,
            String content) {

        int longestRun = 0;
        int currentRun = 0;

        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '`') {
                currentRun++;
                longestRun = Math.max(longestRun, currentRun);
            } else {
                currentRun = 0;
            }
        }

        String fence = "`".repeat(
                Math.max(3, longestRun + 1)
        );

        report.append("## ")
                .append(heading)
                .append("\n\n")
                .append(fence)
                .append("text\n")
                .append(content)
                .append("\n")
                .append(fence)
                .append("\n\n");
    }


private void appendCode(
        StringBuilder report,
        String heading,
        String code) {

    if (code.isBlank()) {
        return;
    }

    int longestRun = 0;
    int currentRun = 0;

    for (int i = 0; i < code.length(); i++) {
        if (code.charAt(i) == '`') {
            currentRun++;
            longestRun = Math.max(longestRun, currentRun);
        } else {
            currentRun = 0;
        }
    }

    String fence = "`".repeat(
            Math.max(3, longestRun + 1)
    );

    report.append("## ")
            .append(heading)
            .append("\n\n")
            .append(fence)
            .append("java\n")
            .append(code)
            .append("\n")
            .append(fence)
            .append("\n\n");
}

}
