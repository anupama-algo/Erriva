
package com.erriva.service;

import com.erriva.model.ErrorContext;
import com.erriva.parser.JavaStackTraceParser;

public final class ContextExtractionService {

    private final JavaStackTraceParser parser;

    public ContextExtractionService() {
        this.parser = new JavaStackTraceParser();
    }

    public ErrorContext extract(String errorMessage) {
        return parser.parse(errorMessage);
    }
}
