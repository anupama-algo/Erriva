
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


## 8. Error Knowledge System

The Java engine separates reusable error knowledge from the analysis
of a specific error message.

### 8.1 ErrorKnowledge

An immutable Java model that stores:

- Unique error ID
- Error category
- Error title
- Plain-English explanation
- Possible causes
- Debugging steps
- Incorrect Java code example
- Corrected Java code example

Unlike ErrorAnalysis, ErrorKnowledge does not store the original
user-submitted error message.

### 8.2 ErrorKnowledgeRepository

A Java class responsible for storing and retrieving predefined
ErrorKnowledge entries.

Knowledge entries will be indexed by unique error IDs using a Map.
The repository will support lookup by ID and retrieval of all entries.

The initial repository will contain common Java compiler and
runtime error families.

### 8.3 Recognition Integration

Future recognizer implementations will identify an error family
from a pasted Java error message, retrieve its knowledge entry,
and construct an ErrorAnalysis result.

Unrecognized messages will be handled by a fallback mechanism.

### 8.4 Design Principles

- Offline-first: no network requests are required for error knowledge.
- Reusable: one knowledge entry can explain multiple error messages.
- Immutable: knowledge entries cannot be modified after creation.
- Extensible: new error families can be added without redesigning
  the ErrorAnalysis model.
- Testable: repository entries and lookups will have JUnit tests.

## 9. Runtime Error Recognition

### 9.1 Purpose

The runtime recognition system identifies supported Java runtime
exceptions from error messages and connects them to the Error
Knowledge System.

The recognition process is rule-based and works offline.

### 9.2 Supported Runtime Errors

| Java Exception | Knowledge ID |
| --- | --- |
| NullPointerException | RUNTIME_NULL_POINTER |
| ArrayIndexOutOfBoundsException | RUNTIME_ARRAY_INDEX |
| ArithmeticException | RUNTIME_ARITHMETIC |
| NumberFormatException | RUNTIME_NUMBER_FORMAT |
| ClassCastException | RUNTIME_CLASS_CAST |

### 9.3 Recognition Process

1. Receive a Java error message.
2. Validate that the input is not null or blank.
3. Identify a supported exception name.
4. Match the exception to its knowledge ID.
5. Retrieve the corresponding ErrorKnowledge entry.
6. Construct an ErrorAnalysis result.

### 9.4 Recognition Rules

- Recognize supported exception class names, not arbitrary substrings.
- Support common Java exception formats, including package-qualified names.
- Avoid matching exception names embedded inside longer identifiers.
- Do not treat unrelated mentions of exception names as confirmed errors.
- Preserve the original error message in the analysis result.
- Return a safe fallback for unsupported or unrecognized messages.
- Never execute user-provided Java code.

### 9.5 Design Principles

- Runtime recognition must work without internet access.
- Recognition logic must be separate from stored error knowledge.
- The recognizer must implement the ErrorRecognizer interface.
- Existing ErrorKnowledge entries should be reused.
- New runtime error types should be easy to add later.
- Recognition behavior must be verified with JUnit tests.