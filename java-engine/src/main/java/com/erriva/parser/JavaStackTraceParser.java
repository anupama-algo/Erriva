
package com.erriva.parser;

import com.erriva.model.ErrorContext;
import com.erriva.model.StackTraceFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavaStackTraceParser {

    private static final Pattern EXCEPTION_HEADER = Pattern.compile(
            "^(?:Exception in thread \"[^\"]+\"\\s+)?"
            + "([\\w$]+(?:\\.[\\w$]+)*(?:Exception|Error))"
            + "(?::\\s*(.*))?$"
    );

    private static final Pattern STACK_FRAME = Pattern.compile(
            "^at\\s+(.+)\\.([^.(]+)\\(([^()]*)\\)$"
    );

    private static final Pattern SOURCE_LOCATION = Pattern.compile(
            "^(.+\\.java):(\\d+)$"
    );

    public ErrorContext parse(String stackTrace) {

        if (stackTrace == null || stackTrace.isBlank()) {
            return ErrorContext.empty();
        }

        String exceptionType = null;
        String exceptionMessage = null;

        List<StackTraceFrame> frames = new ArrayList<>();

        String[] lines = stackTrace.split("\\R");

        for (String line : lines) {

            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty()) {
                continue;
            }

            if (exceptionType == null) {

                Matcher headerMatcher =
                        EXCEPTION_HEADER.matcher(trimmedLine);

                if (headerMatcher.matches()) {
                    exceptionType = headerMatcher.group(1);
                    exceptionMessage = headerMatcher.group(2);
                    continue;
                }
            }

            Matcher frameMatcher =
                    STACK_FRAME.matcher(trimmedLine);

            if (!frameMatcher.matches()) {
                continue;
            }

            String className = frameMatcher.group(1);

            // Remove Java module or class-loader prefixes, if present.
            int moduleSeparator = className.lastIndexOf('/');

            if (moduleSeparator >= 0) {
                className = className.substring(moduleSeparator + 1);
            }

            String methodName = frameMatcher.group(2);
            String source = frameMatcher.group(3);

            String fileName = null;
            int lineNumber = -1;

            if (!source.equals("Unknown Source")
                    && !source.equals("Native Method")) {

                Matcher sourceMatcher =
                        SOURCE_LOCATION.matcher(source);

                if (!sourceMatcher.matches()) {
                    continue;
                }

                fileName = sourceMatcher.group(1);

                try {
                    lineNumber = Integer.parseInt(
                            sourceMatcher.group(2)
                    );
                } catch (NumberFormatException exception) {
                    continue;
                }

                if (lineNumber <= 0) {
                    continue;
                }
            }

            if (className.isBlank() || methodName.isBlank()) {
                continue;
            }

            frames.add(
                    new StackTraceFrame(
                            className,
                            methodName,
                            fileName,
                            lineNumber
                    )
            );
        }

        return new ErrorContext(
                exceptionType,
                exceptionMessage,
                frames
        );
    }
}
