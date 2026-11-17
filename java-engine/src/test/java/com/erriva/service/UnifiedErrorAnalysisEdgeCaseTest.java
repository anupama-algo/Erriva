
package com.erriva.service;

import com.erriva.model.ErrorCategory;
import com.erriva.model.UnifiedAnalysisResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedErrorAnalysisEdgeCaseTest {

    private final UnifiedErrorAnalysisService service =
            new UnifiedErrorAnalysisService();

    @Test
    void handlesMultilineUnsupportedMessage() {
        String input = """
                Unexpected application failure
                Additional diagnostic information
                Please contact support
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());
        assertEquals(input,
                result.getAnalysis().getOriginalMessage());
        assertNotNull(result.getContext());
    }

    @Test
    void handlesUnicodeAndSpecialCharacters() {
        String input = "Unexpected error: नमस्ते 日本語 🚀 @#$%";

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());
        assertEquals(input,
                result.getAnalysis().getOriginalMessage());
    }

    @Test
    void handlesNewlineOnlyInput() {
        String input = "\n\r\n\t";

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());
        assertFalse(result.getContext().hasFrames());
    }

    @Test
    void handlesVeryLongUnsupportedMessage() {
        String input = "Unexpected diagnostic ".repeat(1000);

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());
        assertEquals(input,
                result.getAnalysis().getOriginalMessage());
    }

    @Test
    void supportsRepeatedAnalysisCalls() {
        String input =
                "java.lang.ArithmeticException: / by zero";

        for (int i = 0; i < 20; i++) {
            UnifiedAnalysisResult result = service.analyze(input);

            assertNotNull(result);
            assertEquals(ErrorCategory.RUNTIME,
                    result.getAnalysis().getCategory());
        }
    }

    @Test
    void handlesDifferentInputsSequentially() {
        UnifiedAnalysisResult compiler = service.analyze(
                "Main.java:3: error: cannot find symbol"
        );

        UnifiedAnalysisResult runtime = service.analyze(
                "java.lang.NullPointerException"
        );

        UnifiedAnalysisResult unknown = service.analyze(
                "Unknown diagnostic message"
        );

        assertAll(
                () -> assertEquals(ErrorCategory.COMPILER,
                        compiler.getAnalysis().getCategory()),
                () -> assertEquals(ErrorCategory.RUNTIME,
                        runtime.getAnalysis().getCategory()),
                () -> assertEquals(ErrorCategory.UNKNOWN,
                        unknown.getAnalysis().getCategory())
        );
    }


    @Test
    void acceptsInputExactlyAtMaximumLength() {
        String input = "x".repeat(
                ErrorInputValidator.MAX_INPUT_LENGTH
        );

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(
                ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory()
        );
    }

    @Test
    void rejectsInputExceedingMaximumLength() {
        String input = "x".repeat(
                ErrorInputValidator.MAX_INPUT_LENGTH + 1
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.analyze(input)
        );

        assertTrue(
                exception.getMessage().contains("100000")
        );
    }

    @Test
    void rejectsOversizedRecognizableRuntimeError() {
        String input =
                "java.lang.NullPointerException"
                + "x".repeat(
                        ErrorInputValidator.MAX_INPUT_LENGTH
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyze(input)
        );
    }

    @Test
    void preservesUnknownFallbackForBlankInput() {
        UnifiedAnalysisResult result = service.analyze(" \n\t ");

        assertNotNull(result);
        assertEquals(
                ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory()
        );
        assertNotNull(result.getContext());
    }

}
