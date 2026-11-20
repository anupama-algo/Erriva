
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

    @Test
    void recognizesWindowsFilePathDiagnostic() {
        String message =
                "C:\\Users\\Student\\Projects\\Main.java:12: error: cannot find symbol";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
    }

    @Test
    void recognizesMultilineCannotFindSymbolDiagnostic() {
        String message =
                "Main.java:5: error: cannot find symbol\n"
                + "        System.out.println(total);\n"
                + "                           ^\n"
                + "  symbol:   variable total\n"
                + "  location: class Main\n"
                + "1 error";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesIllegalStartOfExpressionDiagnostic() {
        String message =
                "Main.java:8: error: illegal start of expression\n"
                + "        public void display() {\n"
                + "        ^\n"
                + "1 error";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getDebuggingSteps().isEmpty());
    }

    @Test
    void recognizesReachedEndOfFileDiagnostic() {
        String message =
                "Main.java:15: error: reached end of file while parsing\n"
                + "}\n"
                + " ^\n"
                + "1 error";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesNonStaticVariableDiagnostic() {
        String message =
                "Main.java:10: error: non-static variable count "
                + "cannot be referenced from a static context";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesNonStaticMethodDiagnostic() {
        String message =
                "Main.java:10: error: non-static method display() "
                + "cannot be referenced from a static context";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void recognizesFirstSupportedErrorInMultipleDiagnostics() {
        String message =
                "Main.java:5: error: cannot find symbol\n"
                + "        System.out.println(total);\n"
                + "                           ^\n"
                + "Main.java:8: error: ';' expected\n"
                + "2 errors";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
        assertEquals(message, result.getOriginalMessage());
    }

    @Test
    void rejectsUnsupportedCompilerDiagnostic() {
        String message =
                "Main.java:4: error: package example.missing does not exist";

        assertFalse(recognizer.supports(message));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze(message)
        );
    }
}
