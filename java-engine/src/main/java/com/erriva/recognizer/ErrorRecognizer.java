
package com.erriva.recognizer;

import com.erriva.model.ErrorAnalysis;

/**
 * Defines the contract for recognizing and analyzing
 * a specific family of Java error messages.
 */
public interface ErrorRecognizer {

    /**
     * Checks whether this recognizer supports the error.
     *
     * @param errorMessage the Java error message
     * @return true if the error is recognized
     */
    boolean supports(String errorMessage);

    /**
     * Produces a structured explanation for an error
     * supported by this recognizer.
     *
     * @param errorMessage the Java error message
     * @return the completed error analysis
     */
    ErrorAnalysis analyze(String errorMessage);
}
