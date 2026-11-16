
package com.erriva.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnifiedAnalysisResultTest {

    private ErrorAnalysis createAnalysis() {
        return new ErrorAnalysis(
                "java.lang.NullPointerException",
                ErrorCategory.RUNTIME,
                "Null Pointer Exception",
                "An object reference is null.",
                List.of("The object was not initialized."),
                List.of("Check the object before using it."),
                "String text = null; text.length();",
                "String text = \"Hello\"; text.length();"
        );
    }

    @Test
    void constructorStoresAnalysis() {
        ErrorAnalysis analysis = createAnalysis();
        ErrorContext context = ErrorContext.empty();

        UnifiedAnalysisResult result =
                new UnifiedAnalysisResult(analysis, context);

        assertSame(analysis, result.getAnalysis());
    }

    @Test
    void constructorStoresContext() {
        ErrorAnalysis analysis = createAnalysis();
        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Cannot invoke a method because an object is null",
                List.of(
                        new StackTraceFrame(
                                "com.example.Main",
                                "main",
                                "Main.java",
                                12
                        )
                )
        );

        UnifiedAnalysisResult result =
                new UnifiedAnalysisResult(analysis, context);

        assertSame(context, result.getContext());
    }

    @Test
    void constructorRejectsNullAnalysis() {
        ErrorContext context = ErrorContext.empty();

        assertThrows(
                IllegalArgumentException.class,
                () -> new UnifiedAnalysisResult(null, context)
        );
    }

    @Test
    void constructorRejectsNullContext() {
        ErrorAnalysis analysis = createAnalysis();

        assertThrows(
                IllegalArgumentException.class,
                () -> new UnifiedAnalysisResult(analysis, null)
        );
    }

    @Test
    void gettersPreserveOriginalReferences() {
        ErrorAnalysis analysis = createAnalysis();
        ErrorContext context = ErrorContext.empty();

        UnifiedAnalysisResult result =
                new UnifiedAnalysisResult(analysis, context);

        assertAll(
                () -> assertSame(analysis, result.getAnalysis()),
                () -> assertSame(context, result.getContext())
        );
    }

    @Test
    void resultPreservesEmptyContext() {
        ErrorAnalysis analysis = createAnalysis();

        UnifiedAnalysisResult result =
                new UnifiedAnalysisResult(
                        analysis,
                        ErrorContext.empty()
                );

        assertNotNull(result.getContext());
        assertFalse(result.getContext().hasException());
        assertFalse(result.getContext().hasFrames());
        assertEquals(0, result.getContext().getFrameCount());
    }
}
