
package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerAnalysisServiceTest {

    private CompilerAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new CompilerAnalysisService();
    }

    @Test
    void recognizesSupportedCompilerError() {
        String message =
                "Main.java:5: error: cannot find symbol";

        assertTrue(service.supports(message));

        ErrorAnalysis result = service.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void returnsUnknownForUnsupportedError() {
        String message =
                "Main.java:4: error: package example.missing does not exist";

        assertFalse(service.supports(message));

        ErrorAnalysis result = service.analyze(message);

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals("Unrecognized Compiler Error", result.getTitle());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void handlesNullInputSafely() {
        assertFalse(service.supports(null));

        ErrorAnalysis result = assertDoesNotThrow(
                () -> service.analyze(null)
        );

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals(
                "No error message provided",
                result.getOriginalMessage()
        );
    }

    @Test
    void handlesEmptyInputSafely() {
        assertFalse(service.supports(""));

        ErrorAnalysis result = assertDoesNotThrow(
                () -> service.analyze("")
        );

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals(
                "No error message provided",
                result.getOriginalMessage()
        );
    }

    @Test
    void handlesWhitespaceInputSafely() {
        assertFalse(service.supports("   "));

        ErrorAnalysis result = assertDoesNotThrow(
                () -> service.analyze("   ")
        );

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals(
                "No error message provided",
                result.getOriginalMessage()
        );
    }

    @Test
    void preservesUnsupportedOriginalMessage() {
        String message =
                "Main.java:9: error: unsupported compiler diagnostic";

        ErrorAnalysis result = service.analyze(message);

        assertEquals(message, result.getOriginalMessage());
        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
    }

    @Test
    void providesGuidanceForUnknownErrors() {
        ErrorAnalysis result =
                service.analyze("Some unsupported compiler message");

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertFalse(result.getPossibleCauses().isEmpty());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }
}
