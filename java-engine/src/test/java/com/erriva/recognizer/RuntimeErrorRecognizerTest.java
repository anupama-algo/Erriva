
package com.erriva.recognizer;

import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.knowledge.RuntimeErrorKnowledge;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeErrorRecognizerTest {

    private RuntimeErrorRecognizer recognizer;

    @BeforeEach
    void setUp() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        RuntimeErrorKnowledge.registerAll(repository);

        recognizer = new RuntimeErrorRecognizer(repository);
    }

    @Test
    void recognizesNullPointerException() {
        assertTrue(recognizer.supports(
                "java.lang.NullPointerException"));
    }

    @Test
    void recognizesArrayIndexOutOfBoundsException() {
        assertTrue(recognizer.supports(
                "java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds"));
    }

    @Test
    void recognizesArithmeticException() {
        assertTrue(recognizer.supports(
                "java.lang.ArithmeticException: / by zero"));
    }

    @Test
    void recognizesNumberFormatException() {
        assertTrue(recognizer.supports(
                "java.lang.NumberFormatException: For input string: \"abc\""));
    }

    @Test
    void recognizesClassCastException() {
        assertTrue(recognizer.supports(
                "java.lang.ClassCastException: class java.lang.String cannot be cast"));
    }

    @Test
    void rejectsInvalidAndUnsupportedMessages() {
        assertFalse(recognizer.supports(null));
        assertFalse(recognizer.supports(""));
        assertFalse(recognizer.supports("   "));
        assertFalse(recognizer.supports(
                "java.lang.UnsupportedOperationException"));
        assertFalse(recognizer.supports(
                "SomeNullPointerException"));
        assertFalse(recognizer.supports(
                "NullPointerExceptionExample"));
        assertFalse(recognizer.supports(
                "This documentation mentions NullPointerException."));
    }

    @Test
    void producesValidRuntimeAnalysis() {
        String message = "java.lang.ArithmeticException: / by zero";

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
        assertFalse(result.getTitle().isBlank());
        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getPossibleCauses().isEmpty());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }
}
