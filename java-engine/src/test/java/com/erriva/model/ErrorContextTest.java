
package com.erriva.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorContextTest {

    private StackTraceFrame createFrame() {
        return new StackTraceFrame(
                "com.example.Main",
                "main",
                "Main.java",
                10
        );
    }

    @Test
    void shouldStoreExceptionInformation() {
        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Something was null",
                List.of(createFrame())
        );

        assertEquals(
                "java.lang.NullPointerException",
                context.getExceptionType()
        );
        assertEquals(
                "Something was null",
                context.getExceptionMessage()
        );
        assertTrue(context.hasException());
    }

    @Test
    void shouldStoreMultipleFrames() {
        StackTraceFrame first = createFrame();

        StackTraceFrame second = new StackTraceFrame(
                "com.example.Student",
                "getName",
                "Student.java",
                24
        );

        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Error",
                List.of(first, second)
        );

        assertEquals(2, context.getFrameCount());
        assertTrue(context.hasFrames());
        assertSame(first, context.getFirstFrame());
        assertSame(second, context.getFrames().get(1));
    }

    @Test
    void shouldSupportEmptyFrameList() {
        ErrorContext context = new ErrorContext(
                "java.lang.ArithmeticException",
                "/ by zero",
                List.of()
        );

        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
        assertNull(context.getFirstFrame());
    }

    @Test
    void shouldCreateEmptyContext() {
        ErrorContext context = ErrorContext.empty();

        assertNull(context.getExceptionType());
        assertNull(context.getExceptionMessage());
        assertFalse(context.hasException());
        assertFalse(context.hasFrames());
        assertEquals(0, context.getFrameCount());
        assertNull(context.getFirstFrame());
    }

    @Test
    void shouldRejectBlankExceptionType() {
        assertThrows(IllegalArgumentException.class, () ->
                new ErrorContext("  ", "Error", List.of())
        );
    }

    @Test
    void shouldRejectNullFrameList() {
        assertThrows(IllegalArgumentException.class, () ->
                new ErrorContext(
                        "java.lang.NullPointerException",
                        "Error",
                        null
                )
        );
    }

    @Test
    void shouldRejectNullFrameElements() {
        List<StackTraceFrame> frames = new ArrayList<>();
        frames.add(createFrame());
        frames.add(null);

        assertThrows(IllegalArgumentException.class, () ->
                new ErrorContext(
                        "java.lang.NullPointerException",
                        "Error",
                        frames
                )
        );
    }

    @Test
    void shouldProtectAgainstChangesToOriginalList() {
        List<StackTraceFrame> frames = new ArrayList<>();
        frames.add(createFrame());

        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Error",
                frames
        );

        frames.clear();

        assertEquals(1, context.getFrameCount());
        assertTrue(context.hasFrames());
    }

    @Test
    void shouldReturnUnmodifiableFrameList() {
        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                "Error",
                List.of(createFrame())
        );

        assertThrows(UnsupportedOperationException.class, () ->
                context.getFrames().clear()
        );

        assertEquals(1, context.getFrameCount());
    }

    @Test
    void shouldAllowMissingExceptionType() {
        ErrorContext context = new ErrorContext(
                null,
                null,
                List.of(createFrame())
        );

        assertFalse(context.hasException());
        assertTrue(context.hasFrames());
        assertEquals(1, context.getFrameCount());
    }

    @Test
    void shouldAllowMissingExceptionMessage() {
        ErrorContext context = new ErrorContext(
                "java.lang.NullPointerException",
                null,
                List.of()
        );

        assertTrue(context.hasException());
        assertNull(context.getExceptionMessage());
    }

    @Test
    void shouldPreserveFrameOrder() {
        StackTraceFrame first = createFrame();

        StackTraceFrame second = new StackTraceFrame(
                "com.example.Helper",
                "run",
                "Helper.java",
                30
        );

        ErrorContext context = new ErrorContext(
                "java.lang.RuntimeException",
                "Failure",
                List.of(first, second)
        );

        assertSame(first, context.getFrames().get(0));
        assertSame(second, context.getFrames().get(1));
    }
}
