
package com.erriva.service;

import com.erriva.model.InputValidationResult;

/**
 * Validates Java error messages before analysis.
 */
public final class ErrorInputValidator {

    public static final int MAX_INPUT_LENGTH = 100_000;

    /**
     * Validates the provided Java error message.
     *
     * @param errorMessage the input to validate
     * @return the validation result
     */
    public InputValidationResult validate(String errorMessage) {

        if (errorMessage == null || errorMessage.isBlank()) {
            return InputValidationResult.invalid(
                    "Please provide a Java error message."
            );
        }

        if (errorMessage.length() > MAX_INPUT_LENGTH) {
            return InputValidationResult.invalid(
                    "The error message exceeds the maximum allowed "
                            + "length of " + MAX_INPUT_LENGTH
                            + " characters."
            );
        }

        return InputValidationResult.valid();
    }
}
