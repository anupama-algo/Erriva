
# Erriva

### Understand the error. Own the fix.

Erriva is an offline-first Chrome extension designed to help beginner Java programmers understand error messages and learn how to debug them.

Instead of simply identifying an error, Erriva aims to explain what happened, why it might have happened, what to check, and what a possible correction looks like.


## Project Overview

- **Language focus:** Java
- **Project type:** Chrome Extension + Java OOP Engine
- **Development status:** In Progress
- **Target release:** November 27, 2026


## Planned Features

- **Error Explanation:** Understand supported Java compiler errors and runtime exceptions in simple language.
- **Context Extraction:** Identify useful details from error messages, such as invalid indices, missing symbols, or problematic input values.
- **Why It Happened:** Understand the meaning of an error and its possible causes.
- **Fix Guidance:** Follow practical debugging steps.
- **Before & After Examples:** Learn from small incorrect and corrected Java examples.
- **Fix Path:** Work through a short debugging checklist.
- **Error Journal:** Save explanations locally for future reference.
- **Unknown Error Handling:** Receive an honest fallback when an error cannot be confidently recognized.
- **Selected-Text Shortcut:** Explain highlighted error messages through the browser's context menu.

## Technology Stack

### Java Engine
- Java 21 LTS
- Object-Oriented Programming
- Multithreading using ExecutorService
- Maven
- JUnit for testing

### Chrome Extension
- HTML5
- CSS3
- JavaScript
- JSON
- Chrome Extension Manifest V3
- Chrome Storage API

### Development Tools
- Visual Studio Code
- Git
- GitHub

## Project Structure

```text
Erriva/
├── java-engine/   # Java OOP analysis engine and concurrent batch analysis
├── extension/     # Offline Chrome extension
├── shared/        # Shared error knowledge and test cases
├── docs/          # Architecture and testing documentation
├── README.md
├── LICENSE
└── .gitignore
```

## How Erriva Will Work

1. Copy a Java error message from your coding environment.
2. Open the Erriva Chrome extension.
3. Paste the error message.
4. Select **Explain Error**.
5. Review the explanation, possible causes, debugging steps, and example correction.
6. Apply the relevant changes to your own code.

The Chrome extension will run locally using JavaScript and structured error knowledge.

The separate Java 21 engine will demonstrate object-oriented error analysis and multithreaded batch processing. Both implementations will be tested against shared error cases.

**Note:** The browser extension does not execute Java classes directly.

## Project Scope

Erriva is an educational error-explanation tool, not an automatic Java code fixer.

Because it analyzes error messages without requiring source code, it cannot always determine the exact cause or guarantee a correction for a particular program.

## Installation

Installation instructions will be added when the Chrome extension is ready.

## Testing

Java unit tests and extension verification instructions will be documented as development progresses.

## Roadmap

- [ ] Initialize repository and development environment
- [ ] Build Java OOP analysis engine
- [ ] Implement Java multithreaded batch analysis
- [ ] Create Chrome extension interface
- [ ] Implement error recognition and explanations
- [ ] Add debugging steps and corrected examples
- [ ] Add Error Journal and browser integration
- [ ] Complete testing and documentation
- [ ] Prepare version 1.0.0 release

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
