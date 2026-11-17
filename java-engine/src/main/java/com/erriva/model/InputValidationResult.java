
package com.erriva.model;

/**
 * Represents the result of validating an error message.
 */
public final class InputValidationResult {

    private final boolean valid;
    private final String message;

    private InputValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static InputValidationResult valid() {
        return new InputValidationResult(true, "");
    }

    public static InputValidationResult invalid(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Validation message cannot be empty."
            );
        }

        return new InputValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
