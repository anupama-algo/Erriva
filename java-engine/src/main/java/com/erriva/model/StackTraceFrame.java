
package com.erriva.model;

public final class StackTraceFrame {

    private final String className;
    private final String methodName;
    private final String fileName;
    private final int lineNumber;

    public StackTraceFrame(
            String className,
            String methodName,
            String fileName,
            int lineNumber) {

        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException(
                    "Class name cannot be null or blank");
        }

        if (methodName == null || methodName.isBlank()) {
            throw new IllegalArgumentException(
                    "Method name cannot be null or blank");
        }

        if (lineNumber < -1) {
            throw new IllegalArgumentException(
                    "Line number cannot be less than -1");
        }

        if (fileName != null && fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be blank");
        }

        if (fileName == null && lineNumber != -1) {
            throw new IllegalArgumentException(
                    "Line number must be -1 when file name is unavailable");
        }

        if (fileName != null && lineNumber == 0) {
            throw new IllegalArgumentException(
                    "Line number must be positive or -1");
        }

        this.className = className;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean hasSourceLocation() {
        return fileName != null && lineNumber > 0;
    }
}
