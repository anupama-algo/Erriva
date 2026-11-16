
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


## 10. Compiler Error Recognition

### 10.1 Purpose

The compiler recognition system identifies supported Java
compilation errors and connects them to the error knowledge
repository.

It uses deterministic pattern matching and works offline.

### 10.2 Supported Compiler Errors

The initial compiler recognizer supports five error types:

1. Cannot Find Symbol
   - Pattern: cannot find symbol
   - Knowledge ID: COMPILER_CANNOT_FIND_SYMBOL

2. Incompatible Types
   - Pattern: incompatible types:
   - Knowledge ID: COMPILER_INCOMPATIBLE_TYPES

3. Missing Semicolon
   - Pattern: ';' expected
   - Knowledge ID: COMPILER_SEMICOLON_EXPECTED

4. Missing Return Statement
   - Pattern: missing return statement
   - Knowledge ID: COMPILER_MISSING_RETURN

5. Uninitialized Variable
   - Pattern: variable ... might not have been initialized
   - Knowledge ID: COMPILER_UNINITIALIZED_VARIABLE

### 10.3 Recognition Process

1. Receive a Java compiler error message.
2. Reject null, empty, or whitespace-only input.
3. Search for a supported compiler diagnostic.
4. Match the diagnostic to its knowledge ID.
5. Retrieve the corresponding ErrorKnowledge entry.
6. Construct an ErrorAnalysis object.
7. Return the analysis to the calling service.

### 10.4 Recognition Rules

- Recognition is case-sensitive.
- Patterns must match actual compiler diagnostic lines.
- Compiler diagnostics may contain file names and line numbers.
- The recognizer must not classify ordinary source code
  or unrelated text as a compiler error.
- Runtime exception messages must not be classified as
  compiler errors.
- Unknown compiler messages remain unsupported.
- Missing knowledge entries produce an explicit error.
- The recognizer never executes user-provided code.

### 10.5 Design Principles

CompilerErrorRecognizer implements the ErrorRecognizer
interface.

It uses ErrorKnowledgeRepository for knowledge lookup
and returns ErrorAnalysis objects.

CompilerAnalysisService will initialize the repository,
register compiler knowledge, and provide safe fallback
behavior for invalid or unsupported input.

The recognition engine remains independent of the
Chrome extension user interface.


## 11. Java Stack Trace and Error Context Extraction

### Purpose

Erriva extracts structured context from pasted Java runtime
stack traces without executing user code.

### StackTraceFrame

An immutable model representing one stack trace frame.

Fields:
- className: String
- methodName: String
- fileName: String (nullable)
- lineNumber: int (-1 when unavailable)

### ErrorContext

An immutable model representing extracted error information.

Fields:
- exceptionType: String (nullable when unavailable)
- exceptionMessage: String (nullable when unavailable)
- frames: List<StackTraceFrame>

The frames list preserves stack trace order and is exposed
as an unmodifiable defensive copy.

### JavaStackTraceParser

The parser reads plain-text Java stack traces and extracts:
- Exception type
- Exception message
- Fully qualified class names
- Method names
- Source file names
- Source line numbers
- Multiple stack frames in their original order

### Parsing Rules

1. Do not execute submitted code.
2. Handle null, blank, and incomplete input safely.
3. Return an empty context when no information is recognized.
4. Skip malformed stack frames without crashing.
5. Preserve the original order of valid frames.
6. Use null for unavailable file names.
7. Use -1 for unavailable line numbers.
8. Recognize Unknown Source and Native Method locations.
9. Treat the first frame as the topmost reported call,
   not necessarily the root cause of the bug.
10. Keep context extraction separate from error recognition.

### Future Integration

A context extraction service will expose the parsed context
to other Erriva components.

The Chrome extension will eventually display this information
alongside plain-English error explanations.


## 12. Unified Java Error Analysis

### Purpose

The unified analysis layer coordinates the existing Java compiler
error recognition, runtime error recognition, and stack trace
context extraction components.

It provides one entry point for analyzing Java error messages
without duplicating the recognition rules.

### Main Components

1. UnifiedAnalysisResult
   - Immutable model containing an ErrorAnalysis and an ErrorContext.
   - Preserves the original analysis category, explanation,
     possible causes, debugging steps, and available stack frames.
   - Rejects null analysis and context objects.

2. UnifiedErrorAnalysisService
   - Coordinates CompilerAnalysisService,
     RuntimeAnalysisService, and ContextExtractionService.
   - Provides a single analyze(String) method.
   - Uses existing supports(String) methods to select a recognizer.

### Recognition Priority

The unified service applies the following order:

1. If the compiler service supports the input, use compiler analysis.
2. Otherwise, if the runtime service supports the input,
   use runtime analysis.
3. Otherwise, return a safe UNKNOWN analysis.

Compiler recognition takes priority if both services support
the same input.

### Context Extraction

ContextExtractionService runs independently of the selected
error category.

It extracts available exception information and stack frames,
including class names, method names, source filenames, and
line numbers.

An UNKNOWN analysis may still contain useful stack trace context.

### Input Handling

- Null, empty, and whitespace-only inputs return an UNKNOWN analysis.
- Unsupported inputs return an UNKNOWN analysis.
- Invalid inputs must not cause the unified service to crash.
- The original input is preserved for nonblank messages.
- No submitted Java code is executed.

### Design Principles

- Reuse existing recognition and parsing components.
- Keep the combined result immutable.
- Maintain offline-first operation.
- Avoid AI APIs, databases, and network dependencies.
- Keep Java engine logic independent from the Chrome extension UI.
- Verify the integration with JUnit 5 tests.