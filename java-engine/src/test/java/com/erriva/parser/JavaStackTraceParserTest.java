
package com.erriva.parser;

import com.erriva.model.ErrorContext;
import com.erriva.model.StackTraceFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaStackTraceParserTest {

    private final JavaStackTraceParser parser =
            new JavaStackTraceParser();

    @Test
    void shouldParseCompleteStackTrace() {
        String trace = """
                Exception in thread "main" java.lang.NullPointerException: name is null
                    at com.example.Student.getName(Student.java:24)
                    at com.example.Main.main(Main.java:10)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.NullPointerException",
                context.getExceptionType());
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
    void shouldPreserveStackFrameOrder() {
        String trace = """
                java.lang.RuntimeException: Failure
                    at com.example.First.run(First.java:5)
                    at com.example.Second.execute(Second.java:20)
                    at com.example.Main.main(Main.java:30)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(3, context.getFrameCount());
        assertEquals("com.example.First",
                context.getFrames().get(0).getClassName());
        assertEquals("com.example.Second",
                context.getFrames().get(1).getClassName());
        assertEquals("com.example.Main",
                context.getFrames().get(2).getClassName());
    }

    @Test
    void shouldParseExceptionWithoutMessage() {
        String trace = """
                java.lang.NullPointerException
                    at com.example.Main.main(Main.java:12)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.NullPointerException",
                context.getExceptionType());
        assertNull(context.getExceptionMessage());
        assertEquals(1, context.getFrameCount());
    }

    @Test
    void shouldParseErrorType() {
        String trace = """
                java.lang.StackOverflowError
                    at com.example.Main.repeat(Main.java:7)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.StackOverflowError",
                context.getExceptionType());
        assertEquals(1, context.getFrameCount());
    }

    @Test
    void shouldHandleNullInput() {
        ErrorContext context = parser.parse(null);

        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldHandleBlankInput() {
        ErrorContext context = parser.parse("   \n  ");

        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldIgnoreMalformedLines() {
        String trace = """
                java.lang.ArithmeticException: / by zero
                random text
                at invalid
                    at com.example.Main.main(Main.java:15)
                not a stack frame
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.ArithmeticException",
                context.getExceptionType());
        assertEquals(1, context.getFrameCount());
        assertEquals(15, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldSupportUnknownSource() {
        String trace = """
                java.lang.RuntimeException: Unknown location
                    at com.example.Main.run(Unknown Source)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(1, context.getFrameCount());

        StackTraceFrame frame = context.getFirstFrame();

        assertEquals("com.example.Main", frame.getClassName());
        assertEquals("run", frame.getMethodName());
        assertNull(frame.getFileName());
        assertEquals(-1, frame.getLineNumber());
        assertFalse(frame.hasSourceLocation());
    }

    @Test
    void shouldSupportNativeMethod() {
        String trace = """
                java.lang.RuntimeException: Native call
                    at java.lang.Thread.sleep(Native Method)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(1, context.getFrameCount());

        StackTraceFrame frame = context.getFirstFrame();

        assertEquals("java.lang.Thread", frame.getClassName());
        assertEquals("sleep", frame.getMethodName());
        assertNull(frame.getFileName());
        assertEquals(-1, frame.getLineNumber());
    }

    @Test
    void shouldExtractFramesWithoutExceptionHeader() {
        String trace = """
                at com.example.Helper.run(Helper.java:9)
                at com.example.Main.main(Main.java:18)
                """;

        ErrorContext context = parser.parse(trace);

        assertFalse(context.hasException());
        assertEquals(2, context.getFrameCount());
        assertEquals("com.example.Helper",
                context.getFirstFrame().getClassName());
    }

    @Test
    void shouldIgnoreInvalidLineNumbers() {
        String trace = """
                java.lang.RuntimeException: Invalid location
                    at com.example.Main.first(Main.java:abc)
                    at com.example.Main.second(Main.java:0)
                    at com.example.Main.third(Main.java:25)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(1, context.getFrameCount());
        assertEquals("third",
                context.getFirstFrame().getMethodName());
        assertEquals(25, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldHandleWindowsLineEndings() {
        String trace =
                "java.lang.ArithmeticException: / by zero\r\n"
                + "\tat com.example.Main.divide(Main.java:11)\r\n";

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.ArithmeticException",
                context.getExceptionType());
        assertEquals(1, context.getFrameCount());
        assertEquals(11, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldHandleEmptyExceptionMessage() {
        String trace = """
                java.lang.RuntimeException:
                    at com.example.Main.main(Main.java:5)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals("java.lang.RuntimeException",
                context.getExceptionType());
        assertEquals(1, context.getFrameCount());
        assertTrue(
                context.getExceptionMessage() == null
                || context.getExceptionMessage().isEmpty()
        );
    }

    @Test
    void shouldIgnoreUnrecognizedText() {
        ErrorContext context = parser.parse(
                "This is not a Java stack trace."
        );

        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
    }

    @Test
    void shouldHandleLargeLineNumberSafely() {
        String trace = """
                java.lang.RuntimeException: Failure
                    at com.example.Main.first(Main.java:999999999999999)
                    at com.example.Main.second(Main.java:42)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(1, context.getFrameCount());
        assertEquals(42, context.getFirstFrame().getLineNumber());
    }

    @Test
    void shouldParseModernJavaModuleStackFrame() {
        String trace = """
                java.lang.RuntimeException: Failure
                    at java.base/java.lang.Thread.run(Thread.java:1583)
                """;

        ErrorContext context = parser.parse(trace);

        assertEquals(1, context.getFrameCount());

        StackTraceFrame frame = context.getFirstFrame();

        assertEquals("java.lang.Thread", frame.getClassName());
        assertEquals("run", frame.getMethodName());
        assertEquals("Thread.java", frame.getFileName());
        assertEquals(1583, frame.getLineNumber());
    }
}
