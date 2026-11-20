
package com.erriva.recognizer;

import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.knowledge.RuntimeErrorKnowledge;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeErrorRecognizerIntegrationTest {

    private ErrorKnowledgeRepository repository;
    private RuntimeErrorRecognizer recognizer;

    @BeforeEach
    void setUp() {
        repository = new ErrorKnowledgeRepository();
        RuntimeErrorKnowledge.registerAll(repository);
        recognizer = new RuntimeErrorRecognizer(repository);
    }

    @Test
    void recognizesNullPointerException() {
        String message =
                "Exception in thread \"main\" java.lang.NullPointerException\n"
                + "    at Main.main(Main.java:12)";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getPossibleCauses().isEmpty());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void recognizesArithmeticException() {
        String message = "java.lang.ArithmeticException: / by zero";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void retrievesCorrectKnowledgeEntry() {
        String message =
                "java.lang.NumberFormatException: For input string: \"abc\"";

        ErrorAnalysis result = recognizer.analyze(message);

        String expectedTitle = repository
                .findById("RUNTIME_NUMBER_FORMAT")
                .orElseThrow()
                .getTitle();

        assertEquals(expectedTitle, result.getTitle());
    }

    @Test
    void rejectsUnsupportedError() {
        String message = "java.lang.UnsupportedOperationException";

        assertFalse(recognizer.supports(message));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze(message)
        );
    }

    @Test
    void recognizesEarliestExceptionInMultipleDiagnostics() {
        String firstMessage =
                "java.lang.ArithmeticException: / by zero";

        String secondMessage =
                "java.lang.NullPointerException";

        String message = firstMessage + "\n" + secondMessage;

        ErrorAnalysis expected = recognizer.analyze(firstMessage);
        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
    }
}
