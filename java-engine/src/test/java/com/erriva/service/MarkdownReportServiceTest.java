
package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;
import com.erriva.model.ErrorContext;
import com.erriva.model.StackTraceFrame;
import com.erriva.model.UnifiedAnalysisResult;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownReportServiceTest {

    private final MarkdownReportService service =
            new MarkdownReportService();

    private UnifiedAnalysisResult createResult(
            ErrorContext context) {

        ErrorAnalysis analysis = new ErrorAnalysis(
                "java.lang.NullPointerException",
                ErrorCategory.RUNTIME,
                "Null Pointer Exception",
                "An object reference is null.",
                List.of("Object was not initialized."),
                List.of("Check the reference before use."),
                "String name = null;\nname.length();",
                "String name = \"Erriva\";\nname.length();"
        );

        return new UnifiedAnalysisResult(analysis, context);
    }

    @Test
    void generatesReportHeading() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertTrue(report.contains(
                "# Erriva - Java Error Analysis Report"));
    }

    @Test
    void includesErrorInformation() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertTrue(report.contains("## Error Title"));
        assertTrue(report.contains("Null Pointer Exception"));
        assertTrue(report.contains("## Category"));
        assertTrue(report.contains("RUNTIME"));
        assertTrue(report.contains(
                "java.lang.NullPointerException"));
    }

    @Test
    void includesExplanationAndCauses() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertTrue(report.contains(
                "An object reference is null."));

        assertTrue(report.contains(
                "- Object was not initialized."));
    }

    @Test
    void includesDebuggingSteps() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertTrue(report.contains("## Debugging Steps"));
        assertTrue(report.contains(
                "- Check the reference before use."));
    }

    @Test
    void includesJavaCodeExamples() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertTrue(report.contains(
                "## Incorrect Example"));

        assertTrue(report.contains(
                "## Corrected Example"));

        assertTrue(report.contains("```java"));
        assertTrue(report.contains(
                "String name = \"Erriva\";"));
    }

    @Test
    void includesExceptionDetails() {
        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Cannot invoke length()",
                List.of()
        );

        String report = service.generate(
                createResult(context));

        assertTrue(report.contains(
                "## Exception Type"));

        assertTrue(report.contains(
                "Cannot invoke length()"));
    }

    @Test
    void includesFormattedStackTrace() {
        StackTraceFrame frame = new StackTraceFrame(
                "Main", "main", "Main.java", 14
        );

        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Reference is null",
                List.of(frame)
        );

        String report = service.generate(
                createResult(context));

        assertTrue(report.contains(
                "at Main.main(Main.java:14)"));
    }

    @Test
    void handlesUnknownSourceFrame() {
        StackTraceFrame frame = new StackTraceFrame(
                "Main", "run", null, -1
        );

        ErrorContext context = new ErrorContext(
                "java.lang.RuntimeException",
                "Failure",
                List.of(frame)
        );

        String report = service.generate(
                createResult(context));

        assertTrue(report.contains(
                "at Main.run(Unknown Source)"));
    }

    @Test
    void omitsEmptyExceptionAndStackTrace() {
        String report = service.generate(
                createResult(ErrorContext.empty()));

        assertFalse(report.contains(
                "## Exception Type"));

        assertFalse(report.contains(
                "## Stack Trace"));
    }

    @Test
    void rejectsNullResult() {
        assertThrows(
                NullPointerException.class,
                () -> service.generate(null)
        );
    }


@Test
void handlesBackticksInsideCodeExamples() {

    ErrorAnalysis analysis = new ErrorAnalysis(
            "Test error",
            ErrorCategory.COMPILER,
            "Markdown Fence Test",
            "Checks code formatting.",
            List.of("Formatting issue."),
            List.of("Inspect the generated report."),
            "String text = \"```\";",
            "String text = \"fixed\";"
    );

    UnifiedAnalysisResult result =
            new UnifiedAnalysisResult(
                    analysis,
                    ErrorContext.empty()
            );

    String report = service.generate(result);

    assertTrue(report.contains(
            "````java\nString text = \"```\";\n````"
    ));

    assertTrue(report.contains(
            "```java\nString text = \"fixed\";\n```"
    ));
}


@Test
void preservesMultilineErrorWithMarkdownCharacters() {

    String originalError =
            "Main.java:5: error: cannot find symbol\n"
            + "    System.out.println(value);\n"
            + "                       ^\n"
            + "# Note: ``` unexpected text";

    ErrorAnalysis analysis = new ErrorAnalysis(
            originalError,
            ErrorCategory.COMPILER,
            "Cannot Find Symbol",
            "The referenced symbol cannot be found.",
            List.of("Variable is not declared."),
            List.of("Check the variable name."),
            "",
            ""
    );

    UnifiedAnalysisResult result =
            new UnifiedAnalysisResult(
                    analysis,
                    ErrorContext.empty()
            );

    String report = service.generate(result);

    assertTrue(report.contains(
            "## Original Error\n\n````text\n"
            + originalError
            + "\n````"
    ));
}


@Test
void preservesBackticksInsideStackTrace() {

    StackTraceFrame frame = new StackTraceFrame(
            "Main`Test",
            "run",
            "Main.java",
            25
    );

    ErrorContext context = new ErrorContext(
            "java.lang.RuntimeException",
            "Unexpected failure",
            List.of(frame)
    );

    String report = service.generate(
            createResult(context)
    );

    assertTrue(report.contains(
            "## Stack Trace\n\n```text\n"
            + "at Main`Test.run(Main.java:25)\n"
            + "```"
    ));
}


}
