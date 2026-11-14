
package com.erriva.recognizer;

import com.erriva.knowledge.CompilerErrorKnowledge;
import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerErrorRecognizerIntegrationTest {

    private CompilerErrorRecognizer recognizer;

    @BeforeEach
    void setUp() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        CompilerErrorKnowledge.registerAll(repository);

        recognizer = new CompilerErrorRecognizer(repository);
    }

    @Test
    void recognizesCannotFindSymbol() {
        String message =
                "Main.java:5: error: cannot find symbol";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertNotNull(result.getTitle());
        assertFalse(result.getExplanation().isBlank());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void recognizesIncompatibleTypes() {
        String message =
                "Main.java:8: error: incompatible types: String cannot be converted to int";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesMissingSemicolon() {
        String message =
                "Main.java:3: error: ';' expected";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void recognizesMissingReturnStatement() {
        String message =
                "Main.java:10: error: missing return statement";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesUninitializedVariable() {
        String message =
                "Main.java:7: error: variable count might not have been initialized";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void preservesOriginalErrorMessage() {
        String message =
                "Main.java:5: error: cannot find symbol";

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(message, result.getOriginalMessage());
    }
}
