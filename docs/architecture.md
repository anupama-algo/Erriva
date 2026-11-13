
# Erriva — System Architecture

## 1. Project Overview

Erriva is an offline-first Java error explanation tool.

It consists of two separate implementations:

1. Chrome Extension — built with HTML, CSS, JavaScript, and JSON.
2. Java Engine — built with Java 21, Maven, and JUnit 5.

The Chrome extension analyzes pasted Java error messages in the browser. The Java engine independently demonstrates object-oriented error analysis and concurrent batch processing.

Java classes do not execute directly inside the Chrome extension.

## 2. Java Engine Packages

### model
Defines structured data types used by the engine.

Planned classes:
- ErrorCategory
- ErrorAnalysis

### recognizer
Contains the contract and implementations for identifying Java error families.

Planned components:
- ErrorRecognizer interface
- Dedicated runtime error recognizers
- Dedicated compiler error recognizers

### knowledge
Contains reusable explanations, likely causes, debugging steps, and small code examples.

### service
Coordinates error recognition and produces structured analysis results.

Responsibilities:
- Accept an error message
- Select a matching recognizer
- Retrieve relevant guidance
- Return an ErrorAnalysis result
- Handle unknown errors gracefully

### concurrent
Provides multithreaded batch analysis using ExecutorService.

Responsibilities:
- Accept multiple error messages
- Analyze them using a bounded thread pool
- Preserve result order
- Handle individual analysis failures safely
- Shut down executor resources correctly

## 3. Analysis Flow

1. Receive a pasted Java error message.
2. Validate the input.
3. Check registered error recognizers.
4. Identify the matching error family.
5. Retrieve the corresponding explanation and debugging guidance.
6. Create a structured ErrorAnalysis result.
7. Return the result to the caller.

If no recognizer matches, return an unknown-error result with general debugging guidance.

## 4. ErrorAnalysis Output

Each analysis result will contain:

- Original error message
- Error category
- Error title
- Plain-English explanation
- Possible causes
- Debugging steps
- Incorrect code example
- Corrected code example

## 5. Object-Oriented Design

- Encapsulation: Models protect their internal state.
- Abstraction: The ErrorRecognizer interface defines a common contract.
- Polymorphism: Different recognizers implement the same interface.
- Composition: Services coordinate recognizers and knowledge components.
- Concurrency: ExecutorService supports parallel batch analysis.

## 6. Chrome Extension Relationship

The browser extension and Java engine are separate implementations.

They will use a shared collection of error cases to check that recognition and explanations remain consistent.

The extension will not claim to execute Java code or invoke Java classes directly.

## 7. Development Principles

- Offline-first operation
- No source-code execution
- No external AI dependency
- Clear and actionable debugging guidance
- Maintainable and testable components
- Honest handling of unrecognized errors
