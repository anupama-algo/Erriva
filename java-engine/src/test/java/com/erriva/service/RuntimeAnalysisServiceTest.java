
package com.erriva.service;

import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeAnalysisServiceTest {

    private RuntimeAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new RuntimeAnalysisService();
    }

    @Test
    void analyzesSupportedRuntimeError() {
        String message = "java.lang.NullPointerException";

        ErrorAnalysis result = service.analyze(message);

        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void handlesNullInput() {
        ErrorAnalysis result = service.analyze(null);

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals("Invalid Error Message", result.getTitle());
        assertEquals("No error message provided", result.getOriginalMessage());
    }

    @Test
    void handlesEmptyInput() {
        ErrorAnalysis result = service.analyze("");

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals("Invalid Error Message", result.getTitle());
    }

    @Test
    void handlesWhitespaceOnlyInput() {
        ErrorAnalysis result = service.analyze("   ");

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals("Invalid Error Message", result.getTitle());
    }

    @Test
    void handlesUnsupportedException() {
        String message = "java.lang.UnsupportedOperationException";

        ErrorAnalysis result = service.analyze(message);

        assertEquals(ErrorCategory.UNKNOWN, result.getCategory());
        assertEquals("Unsupported Java Error", result.getTitle());
        assertEquals(message, result.getOriginalMessage());
    }

    @Test
    void supportsOnlyRecognizedErrors() {
        assertTrue(service.supports(
                "java.lang.ArithmeticException: / by zero"));

        assertFalse(service.supports(
                "java.lang.UnsupportedOperationException"));

        assertFalse(service.supports(null));
        assertFalse(service.supports(""));
    }

    @Test
    void fallbackProvidesUsefulGuidance() {
        ErrorAnalysis result = service.analyze(
                "java.lang.UnsupportedOperationException");

        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getPossibleCauses().isEmpty());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }
}
