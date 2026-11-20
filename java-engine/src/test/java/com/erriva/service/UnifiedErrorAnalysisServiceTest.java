
package com.erriva.service;

import com.erriva.model.ErrorCategory;
import com.erriva.model.UnifiedAnalysisResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedErrorAnalysisServiceTest {

    private final UnifiedErrorAnalysisService service =
            new UnifiedErrorAnalysisService();

    @Test
    void recognizesCannotFindSymbolCompilerError() {
        String input = """
                Main.java:5: error: cannot find symbol
                    System.out.println(value);
                                       ^
                  symbol:   variable value
                  location: class Main
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertNotNull(result);
        assertEquals(ErrorCategory.COMPILER,
                result.getAnalysis().getCategory());
        assertNotNull(result.getContext());
    }

    @Test
    void recognizesIncompatibleTypesCompilerError() {
        String input = """
                Main.java:3: error: incompatible types: String cannot be converted to int
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.COMPILER,
                result.getAnalysis().getCategory());
    }

    @Test
    void recognizesNullPointerException() {
        String input = "java.lang.NullPointerException: value is null";

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.RUNTIME,
                result.getAnalysis().getCategory());
        assertNotNull(result.getAnalysis().getExplanation());
    }

    @Test
    void recognizesArithmeticException() {
        String input = "java.lang.ArithmeticException: / by zero";

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.RUNTIME,
                result.getAnalysis().getCategory());
    }

    @Test
    void extractsRuntimeExceptionContext() {
        String input = """
                Exception in thread "main" java.lang.NullPointerException: value is null
                    at com.example.Main.run(Main.java:12)
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.RUNTIME,
                result.getAnalysis().getCategory());

        assertEquals("java.lang.NullPointerException",
                result.getContext().getExceptionType());

        assertEquals(1, result.getContext().getFrameCount());

        assertEquals("Main.java",
                result.getContext().getFirstFrame().getFileName());

        assertEquals(12,
                result.getContext().getFirstFrame().getLineNumber());
    }

    @Test
    void preservesMultipleStackFrames() {
        String input = """
                java.lang.ArithmeticException: / by zero
                    at com.example.Calculator.divide(Calculator.java:8)
                    at com.example.Main.main(Main.java:20)
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.RUNTIME,
                result.getAnalysis().getCategory());

        assertEquals(2, result.getContext().getFrameCount());

        assertEquals("Calculator",
                result.getContext().getFrames().get(0)
                        .getClassName().substring(
                                "com.example.".length()));

        assertEquals("Main.java",
                result.getContext().getFrames().get(1).getFileName());
    }

    @Test
    void preservesContextForUnknownException() {
        String input = """
                com.example.CustomException: custom failure
                    at com.example.Main.main(Main.java:30)
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());

        assertEquals("com.example.CustomException",
                result.getContext().getExceptionType());

        assertEquals(1, result.getContext().getFrameCount());
    }

    @Test
    void returnsUnknownForUnsupportedText() {
        String input = "Something unexpected happened";

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());

        assertEquals(input,
                result.getAnalysis().getOriginalMessage());
    }

    @Test
    void handlesNullInput() {
        UnifiedAnalysisResult result = service.analyze(null);

        assertNotNull(result);
        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());

        assertNotNull(result.getContext());
        assertFalse(result.getContext().hasException());
    }

    @Test
    void handlesEmptyInput() {
        UnifiedAnalysisResult result = service.analyze("");

        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());

        assertFalse(result.getContext().hasFrames());
    }

    @Test
    void handlesWhitespaceOnlyInput() {
        UnifiedAnalysisResult result = service.analyze("   ");

        assertEquals(ErrorCategory.UNKNOWN,
                result.getAnalysis().getCategory());

        assertEquals(0, result.getContext().getFrameCount());
    }

    @Test
    void skipsMalformedStackFrames() {
        String input = """
                java.lang.NullPointerException
                    at broken frame without parentheses
                    at com.example.Main.main(Main.java:15)
                """;

        UnifiedAnalysisResult result = service.analyze(input);

        assertEquals(ErrorCategory.RUNTIME,
                result.getAnalysis().getCategory());

        assertEquals(1, result.getContext().getFrameCount());

        assertEquals(15,
                result.getContext().getFirstFrame().getLineNumber());
    }


@Test
void routesUnsupportedCompilerDiagnosticToCompilerFallback() {
    String input =
            "Main.java:4: error: package example.missing does not exist";

    UnifiedAnalysisResult result = service.analyze(input);

    assertEquals(ErrorCategory.UNKNOWN,
            result.getAnalysis().getCategory());

    assertEquals("Unrecognized Compiler Error",
            result.getAnalysis().getTitle());

    assertEquals(input,
            result.getAnalysis().getOriginalMessage());
}

@Test
void routesCompilerDiagnosticWithSpacesInPathToCompilerFallback() {
    String input =
            "C:\\Users\\Anupama Sethi\\Project Folder\\Main.java:4: "
                    + "error: package example.missing does not exist";

    UnifiedAnalysisResult result = service.analyze(input);

    assertEquals(ErrorCategory.UNKNOWN,
            result.getAnalysis().getCategory());
    assertEquals("Unrecognized Compiler Error",
            result.getAnalysis().getTitle());
    assertEquals(input,
            result.getAnalysis().getOriginalMessage());
}

}
