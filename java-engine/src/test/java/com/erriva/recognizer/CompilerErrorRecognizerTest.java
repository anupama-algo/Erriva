
package com.erriva.recognizer;

import com.erriva.knowledge.CompilerErrorKnowledge;
import com.erriva.knowledge.ErrorKnowledgeRepository;
import com.erriva.model.ErrorAnalysis;
import com.erriva.model.ErrorCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerErrorRecognizerTest {

    private CompilerErrorRecognizer recognizer;

    @BeforeEach
    void setUp() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        CompilerErrorKnowledge.registerAll(repository);

        recognizer = new CompilerErrorRecognizer(repository);
    }

    @Test
    void recognizesStandardCompilerDiagnostic() {
        String message =
                "Main.java:5: error: cannot find symbol";

        assertTrue(recognizer.supports(message));

        ErrorAnalysis result = recognizer.analyze(message);

        assertEquals(ErrorCategory.COMPILER, result.getCategory());
    }

    @Test
    void rejectsNullInput() {
        assertFalse(recognizer.supports(null));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze(null)
        );
    }

    @Test
    void rejectsBlankInput() {
        assertFalse(recognizer.supports("   "));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze("   ")
        );
    }

    @Test
    void rejectsUnrelatedMessage() {
        String message = "This is not a Java compiler error";

        assertFalse(recognizer.supports(message));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze(message)
        );
    }

    @Test
    void rejectsRuntimeException() {
        String message =
                "Exception in thread \"main\" "
                + "java.lang.NullPointerException";

        assertFalse(recognizer.supports(message));
    }

    @Test
    void usesCaseSensitiveMatching() {
        String message = "error: CANNOT FIND SYMBOL";

        assertFalse(recognizer.supports(message));
    }

    @Test
    void rejectsOrdinaryTextContainingDiagnosticWords() {
        String message =
                "My teacher explained cannot find symbol yesterday.";

        assertFalse(recognizer.supports(message));
    }

    @Test
    void throwsWhenKnowledgeEntryIsMissing() {
        ErrorKnowledgeRepository emptyRepository =
                new ErrorKnowledgeRepository();

        CompilerErrorRecognizer emptyRecognizer =
                new CompilerErrorRecognizer(emptyRepository);

        String message = "error: cannot find symbol";

        assertTrue(emptyRecognizer.supports(message));

        assertThrows(
                IllegalStateException.class,
                () -> emptyRecognizer.analyze(message)
        );
    }

    @Test
    void rejectsUnsupportedCompilerDiagnostic() {
        String message =
                "Main.java:8: error: illegal start of expression";

        assertFalse(recognizer.supports(message));

        assertThrows(
                IllegalArgumentException.class,
                () -> recognizer.analyze(message)
        );
    }

    @Test
    void rejectsNullRepository() {
        assertThrows(
                NullPointerException.class,
                () -> new CompilerErrorRecognizer(null)
        );
    }
}
