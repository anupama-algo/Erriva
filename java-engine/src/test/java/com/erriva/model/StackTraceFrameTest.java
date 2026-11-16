
package com.erriva.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackTraceFrameTest {

    @Test
    void shouldStoreValidStackFrame() {
        StackTraceFrame frame = new StackTraceFrame(
                "com.example.Student",
                "getName",
                "Student.java",
                24
        );

        assertEquals("com.example.Student", frame.getClassName());
        assertEquals("getName", frame.getMethodName());
        assertEquals("Student.java", frame.getFileName());
        assertEquals(24, frame.getLineNumber());
        assertTrue(frame.hasSourceLocation());
    }

    @Test
    void shouldSupportUnknownSource() {
        StackTraceFrame frame = new StackTraceFrame(
                "java.lang.Object",
                "wait",
                null,
                -1
        );

        assertNull(frame.getFileName());
        assertEquals(-1, frame.getLineNumber());
        assertFalse(frame.hasSourceLocation());
    }

    @Test
    void shouldRejectNullClassName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame(null, "run", "Main.java", 10)
        );
    }

    @Test
    void shouldRejectBlankClassName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("  ", "run", "Main.java", 10)
        );
    }

    @Test
    void shouldRejectNullMethodName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", null, "Main.java", 10)
        );
    }

    @Test
    void shouldRejectBlankMethodName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", " ", "Main.java", 10)
        );
    }

    @Test
    void shouldRejectInvalidNegativeLineNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", "run", "Main.java", -2)
        );
    }

    @Test
    void shouldRejectZeroLineNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", "run", "Main.java", 0)
        );
    }

    @Test
    void shouldRejectBlankFileName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", "run", " ", 10)
        );
    }

    @Test
    void shouldRejectLineNumberWithoutFileName() {
        assertThrows(IllegalArgumentException.class, () ->
                new StackTraceFrame("Main", "run", null, 10)
        );
    }

    @Test
    void shouldAllowUnknownLineWithKnownFile() {
        StackTraceFrame frame = new StackTraceFrame(
                "Main",
                "run",
                "Main.java",
                -1
        );

        assertFalse(frame.hasSourceLocation());
        assertEquals(-1, frame.getLineNumber());
    }
}
