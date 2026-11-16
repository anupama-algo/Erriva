
package com.erriva.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ErrorContext {

    private final String exceptionType;
    private final String exceptionMessage;
    private final List<StackTraceFrame> frames;

    public ErrorContext(
            String exceptionType,
            String exceptionMessage,
            List<StackTraceFrame> frames) {

        if (exceptionType != null && exceptionType.isBlank()) {
            throw new IllegalArgumentException(
                    "Exception type cannot be blank");
        }

        if (frames == null) {
            throw new IllegalArgumentException(
                    "Stack trace frames cannot be null");
        }
for (StackTraceFrame frame : frames) {
    if (frame == null) {
        throw new IllegalArgumentException(
                "Stack trace frames cannot contain null");
    }
}

        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;

        this.frames = Collections.unmodifiableList(
                new ArrayList<>(frames)
        );
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public List<StackTraceFrame> getFrames() {
        return frames;
    }

    public boolean hasException() {
        return exceptionType != null;
    }

    public boolean hasFrames() {
        return !frames.isEmpty();
    }

    public int getFrameCount() {
        return frames.size();
    }

    public StackTraceFrame getFirstFrame() {
        if (frames.isEmpty()) {
            return null;
        }

        return frames.get(0);
    }

    public static ErrorContext empty() {
        return new ErrorContext(
                null,
                null,
                Collections.emptyList()
        );
    }
}
