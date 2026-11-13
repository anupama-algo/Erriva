
package com.erriva.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorAnalysisTest {

    private ErrorAnalysis createSampleAnalysis() {
        return new ErrorAnalysis(
                "java.lang.ArithmeticException: / by zero",
                ErrorCategory.RUNTIME,
                "ArithmeticException",
                "An integer division operation attempted to divide by zero.",
                List.of("The divisor is zero."),
                List.of("Check the divisor before dividing."),
                "int result = 10 / 0;",
                "if (divisor != 0) { int result = 10 / divisor; }"
        );
    }

    @Test
    void shouldStoreErrorInformationCorrectly() {
        ErrorAnalysis analysis = createSampleAnalysis();

        assertEquals(
                "java.lang.ArithmeticException: / by zero",
                analysis.getOriginalMessage()
        );

        assertEquals(
                "ArithmeticException",
                analysis.getTitle()
        );

        assertEquals(
                "An integer division operation attempted to divide by zero.",
                analysis.getExplanation()
        );

        assertEquals(
                List.of("The divisor is zero."),
                analysis.getPossibleCauses()
        );

        assertEquals(
                List.of("Check the divisor before dividing."),
                analysis.getDebuggingSteps()
        );

        assertEquals(
                "int result = 10 / 0;",
                analysis.getIncorrectExample()
        );

        assertEquals(
                "if (divisor != 0) { int result = 10 / divisor; }",
                analysis.getCorrectedExample()
        );
    }

    @Test
    void shouldReturnCorrectErrorCategory() {
        ErrorAnalysis analysis = createSampleAnalysis();

        assertEquals(
                ErrorCategory.RUNTIME,
                analysis.getCategory()
        );
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ErrorAnalysis(
                        "java.lang.ArithmeticException: / by zero",
                        ErrorCategory.RUNTIME,
                        " ",
                        "Division by zero occurred.",
                        List.of("Zero divisor."),
                        List.of("Check the divisor."),
                        "",
                        ""
                )
        );
    }

    @Test
    void shouldRejectNullCategory() {
        assertThrows(
                NullPointerException.class,
                () -> new ErrorAnalysis(
                        "java.lang.ArithmeticException: / by zero",
                        null,
                        "ArithmeticException",
                        "Division by zero occurred.",
                        List.of("Zero divisor."),
                        List.of("Check the divisor."),
                        "",
                        ""
                )
        );
    }

    @Test
    void shouldPreventModificationOfCauses() {
        ErrorAnalysis analysis = createSampleAnalysis();

        assertThrows(
                UnsupportedOperationException.class,
                () -> analysis.getPossibleCauses().add("Another cause")
        );
    }

    @Test
    void shouldPreventModificationOfDebuggingSteps() {
        ErrorAnalysis analysis = createSampleAnalysis();

        assertThrows(
                UnsupportedOperationException.class,
                () -> analysis.getDebuggingSteps().clear()
        );
    }

    @Test
    void shouldProtectAgainstChangesToOriginalList() {
        List<String> causes = new ArrayList<>();
        causes.add("Original cause");

        ErrorAnalysis analysis = new ErrorAnalysis(
                "Example error",
                ErrorCategory.UNKNOWN,
                "Unknown Error",
                "The error is not recognized.",
                causes,
                List.of("Review the error message."),
                "",
                ""
        );

        causes.add("Added later");

        assertEquals(
                List.of("Original cause"),
                analysis.getPossibleCauses()
        );
    }

    @Test
    void shouldRejectBlankDebuggingStep() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ErrorAnalysis(
                        "Example error",
                        ErrorCategory.UNKNOWN,
                        "Unknown Error",
                        "The error is not recognized.",
                        List.of("Unknown cause."),
                        List.of(" "),
                        "",
                        ""
                )
        );
    }
}
