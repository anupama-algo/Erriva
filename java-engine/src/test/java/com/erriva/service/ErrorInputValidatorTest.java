
package com.erriva.service;

import com.erriva.model.InputValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorInputValidatorTest {

    private final ErrorInputValidator validator =
            new ErrorInputValidator();

    @Test
    void acceptsNormalErrorMessage() {
        InputValidationResult result = validator.validate(
                "java.lang.NullPointerException"
        );

        assertTrue(result.isValid());
        assertEquals("", result.getMessage());
    }

    @Test
    void rejectsNullInput() {
        InputValidationResult result = validator.validate(null);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
    }

    @Test
    void rejectsEmptyInput() {
        InputValidationResult result = validator.validate("");

        assertFalse(result.isValid());
    }

    @Test
    void rejectsWhitespaceOnlyInput() {
        InputValidationResult result = validator.validate(
                " \n\t\r "
        );

        assertFalse(result.isValid());
    }

    @Test
    void acceptsInputAtMaximumLength() {
        String input = "x".repeat(
                ErrorInputValidator.MAX_INPUT_LENGTH
        );

        InputValidationResult result = validator.validate(input);

        assertTrue(result.isValid());
    }

    @Test
    void rejectsInputAboveMaximumLength() {
        String input = "x".repeat(
                ErrorInputValidator.MAX_INPUT_LENGTH + 1
        );

        InputValidationResult result = validator.validate(input);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("100000"));
    }

    @Test
    void acceptsUnicodeErrorMessage() {
        InputValidationResult result = validator.validate(
                "Error: नमस्ते 日本語 🚀"
        );

        assertTrue(result.isValid());
    }

    @Test
    void acceptsMultilineStackTrace() {
        String input = """
                java.lang.ArithmeticException: / by zero
                    at com.example.Calculator.divide(Calculator.java:8)
                    at com.example.Main.main(Main.java:20)
                """;

        InputValidationResult result = validator.validate(input);

        assertTrue(result.isValid());
    }
}
