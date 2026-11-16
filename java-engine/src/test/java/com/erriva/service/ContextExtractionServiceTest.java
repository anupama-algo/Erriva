
package com.erriva.service;

import com.erriva.model.ErrorContext;
import com.erriva.model.StackTraceFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextExtractionServiceTest {

    private final ContextExtractionService service =
            new ContextExtractionService();

    @Test
    void shouldExtractCompleteStackTrace() {
        String trace = """
                Exception in thread "main" java.lang.NullPointerException: name is null
                    at com.example.Student.getName(Student.java:24)
                    at com.example.Main.main(Main.java:10)
                """;

        ErrorContext context = service.extract(trace);

        assertNotNull(context);
        assertEquals(
                "java.lang.NullPointerException",
                context.getExceptionType()
        );
        assertEquals("name is null", context.getExceptionMessage());
        assertEquals(2, context.getFrameCount());

        StackTraceFrame first = context.getFirstFrame();

        assertNotNull(first);
        assertEquals("com.example.Student", first.getClassName());
        assertEquals("getName", first.getMethodName());
        assertEquals("Student.java", first.getFileName());
        assertEquals(24, first.getLineNumber());
    }

    @Test
    void shouldExtractArithmeticException() {
        String trace = """
                java.lang.ArithmeticException: / by zero
                    at com.example.Calculator.divide(Calculator.java:18)
                """;

        ErrorContext context = service.extract(trace);

        assertEquals(
                "java.lang.ArithmeticException",
                context.getExceptionType()
        );
        assertEquals("/ by zero", context.getExceptionMessage());
        assertEquals(1, context.getFrameCount());
        assertEquals(18, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldPreserveMultipleFrames() {
        String trace = """
                java.lang.RuntimeException: Failure
                    at com.example.First.run(First.java:5)
                    at com.example.Second.execute(Second.java:20)
                    at com.example.Main.main(Main.java:30)
                """;

        ErrorContext context = service.extract(trace);

        assertEquals(3, context.getFrameCount());
        assertEquals(
                "com.example.First",
                context.getFrames().get(0).getClassName()
        );
        assertEquals(
                "com.example.Second",
                context.getFrames().get(1).getClassName()
        );
        assertEquals(
                "com.example.Main",
                context.getFrames().get(2).getClassName()
        );
    }

    @Test
    void shouldHandleNullInput() {
        ErrorContext context = service.extract(null);

        assertNotNull(context);
        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldHandleBlankInput() {
        ErrorContext context = service.extract("   \n   ");

        assertNotNull(context);
        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldHandleUnrecognizedText() {
        ErrorContext context = service.extract(
                "This is not a Java stack trace."
        );

        assertNotNull(context);
        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldExtractFramesWithoutExceptionHeader() {
        String trace = """
                at com.example.Helper.run(Helper.java:9)
                at com.example.Main.main(Main.java:18)
                """;

        ErrorContext context = service.extract(trace);

        assertFalse(context.hasException());
        assertTrue(context.hasFrames());
        assertEquals(2, context.getFrameCount());
        assertEquals(
                "com.example.Helper",
                context.getFirstFrame().getClassName()
        );
    }

    @Test
    void shouldHandleUnknownSource() {
        String trace = """
                java.lang.RuntimeException: Unknown location
                    at com.example.Main.run(Unknown Source)
                """;

        ErrorContext context = service.extract(trace);

        assertEquals(1, context.getFrameCount());

        StackTraceFrame frame = context.getFirstFrame();

        assertEquals("com.example.Main", frame.getClassName());
        assertEquals("run", frame.getMethodName());
        assertNull(frame.getFileName());
        assertEquals(-1, frame.getLineNumber());
        assertFalse(frame.hasSourceLocation());
    }

    @Test
    void shouldIgnoreMalformedLinesAndKeepValidFrames() {
        String trace = """
                java.lang.RuntimeException: Failure
                random text
                    at invalid
                    at com.example.Main.main(Main.java:42)
                """;

        ErrorContext context = service.extract(trace);

        assertEquals(
                "java.lang.RuntimeException",
                context.getExceptionType()
        );
        assertEquals(1, context.getFrameCount());
        assertEquals(42, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldReturnIndependentResultsForDifferentInputs() {
        String firstTrace = """
                java.lang.ArithmeticException: / by zero
                    at com.example.Main.divide(Main.java:10)
                """;

        String secondTrace = """
                java.lang.NullPointerException: missing value
                    at com.example.Student.read(Student.java:25)
                """;

        ErrorContext first = service.extract(firstTrace);
        ErrorContext second = service.extract(secondTrace);

        assertNotSame(first, second);

        assertEquals(
                "java.lang.ArithmeticException",
                first.getExceptionType()
        );
        assertEquals(
                "java.lang.NullPointerException",
                second.getExceptionType()
        );

        assertEquals(10, first.getFirstFrame().getLineNumber());
        assertEquals(25, second.getFirstFrame().getLineNumber());
    }
}
