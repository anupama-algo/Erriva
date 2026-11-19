
package com.erriva.knowledge;

import com.erriva.model.ErrorCategory;
import java.util.List;

/**
 * Registers predefined Java compiler error knowledge.
 */
public final class CompilerErrorKnowledge {

    private CompilerErrorKnowledge() {
        // Utility class: objects are not required.
    }

    public static void registerAll(
            ErrorKnowledgeRepository repository) {

        repository.add(new ErrorKnowledge(
                "COMPILER_CANNOT_FIND_SYMBOL",
                ErrorCategory.COMPILER,
                "Cannot Find Symbol",
                "The compiler cannot resolve a referenced variable, method, or class.",
                List.of(
                        "A variable or method name is misspelled.",
                        "The referenced variable is outside its scope.",
                        "A required class or import is missing."
                ),
                List.of(
                        "Read the symbol name in the compiler message.",
                        "Check spelling and capitalization.",
                        "Verify that the symbol is declared and accessible.",
                        "Check imports and method signatures."
                ),
                "int total = 10;\n"
                        + "System.out.println(totl);",
                "int total = 10;\n"
                        + "System.out.println(total);"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_INCOMPATIBLE_TYPES",
                ErrorCategory.COMPILER,
                "Incompatible Types",
                "A value cannot be assigned or converted to the required type.",
                List.of(
                        "The assigned value has an incompatible type.",
                        "A method returns an unexpected type.",
                        "A conversion requires an explicit cast or different logic."
                ),
                List.of(
                        "Read the required and found types.",
                        "Check the variable declaration.",
                        "Check the expression or method return type.",
                        "Use a valid conversion when appropriate."
                ),
                "int age = \"20\";",
                "int age = Integer.parseInt(\"20\");"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_SEMICOLON_EXPECTED",
                ErrorCategory.COMPILER,
                "Semicolon Expected",
                "The compiler expected a semicolon or encountered invalid syntax.",
                List.of(
                        "A statement is missing its terminating semicolon.",
                        "A nearby syntax mistake confused the parser."
                ),
                List.of(
                        "Check the reported line and the line before it.",
                        "Look for missing semicolons.",
                        "Check parentheses, braces, and surrounding syntax.",
                        "Compile again after correcting the statement."
                ),
                "int number = 10\n"
                        + "System.out.println(number);",
                "int number = 10;\n"
                        + "System.out.println(number);"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_MISSING_RETURN",
                ErrorCategory.COMPILER,
                "Missing Return Statement",
                "A method that must return a value does not return one on every required path.",
                List.of(
                        "A return statement is missing.",
                        "Only some branches return a value.",
                        "A conditional path reaches the end of the method."
                ),
                List.of(
                        "Check the method's declared return type.",
                        "Review all if-else branches.",
                        "Ensure every reachable path returns a compatible value.",
                        "Use void only if the method should not return a value."
                ),
                "static int getNumber() {\n"
                        + "    int number = 10;\n"
                        + "}",
                "static int getNumber() {\n"
                        + "    int number = 10;\n"
                        + "    return number;\n"
                        + "}"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_UNINITIALIZED_VARIABLE",
                ErrorCategory.COMPILER,
                "Variable Might Not Have Been Initialized",
                "A local variable may be used before a value has been assigned.",
                List.of(
                        "A local variable was declared without an initial value.",
                        "A conditional branch does not assign the variable.",
                        "The compiler cannot guarantee assignment before use."
                ),
                List.of(
                        "Find the variable named in the compiler message.",
                        "Check where it is declared.",
                        "Ensure it receives a value on every path before use.",
                        "Initialize it with a suitable value when appropriate."
                ),
                "int score;\n"
                        + "System.out.println(score);",
                "int score = 0;\n"
                        + "System.out.println(score);"
        ));


        repository.add(new ErrorKnowledge(
                "COMPILER_ILLEGAL_START",
                ErrorCategory.COMPILER,
                "Illegal Start of Expression",
                "The compiler encountered code that is not valid in the current location.",
                List.of(
                        "A method was declared inside another method.",
                        "A brace is missing or misplaced.",
                        "An expression contains invalid Java syntax."
                ),
                List.of(
                        "Check the reported line and the lines above it.",
                        "Verify that braces and parentheses are balanced.",
                        "Check whether methods and statements are placed correctly.",
                        "Compile again after correcting the syntax."
                ),
                "public void first() {\n"
                        + "    public void second() {}\n"
                        + "}",
                "public void first() {\n"
                        + "    second();\n"
                        + "}\n"
                        + "public void second() {}"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_REACHED_END_OF_FILE",
                ErrorCategory.COMPILER,
                "Reached End of File While Parsing",
                "The compiler reached the end of the source file while expecting more code.",
                List.of(
                        "A closing brace is missing.",
                        "A class or method body was not closed.",
                        "An earlier syntax error left the code incomplete."
                ),
                List.of(
                        "Check the opening and closing braces.",
                        "Review the end of the file.",
                        "Verify that every class and method body is closed.",
                        "Compile again after fixing the incomplete structure."
                ),
                "class Main {\n"
                        + "    public static void main(String[] args) {\n"
                        + "        System.out.println(\"Hello\");\n"
                        + "    }\n",
                "class Main {\n"
                        + "    public static void main(String[] args) {\n"
                        + "        System.out.println(\"Hello\");\n"
                        + "    }\n"
                        + "}"
        ));

        repository.add(new ErrorKnowledge(
                "COMPILER_NON_STATIC_REFERENCE",
                ErrorCategory.COMPILER,
                "Non-static Reference from Static Context",
                "An instance member was accessed without an object from a static context.",
                List.of(
                        "An instance variable was accessed directly from a static method.",
                        "An instance method was called without creating an object.",
                        "Static and instance members were confused."
                ),
                List.of(
                        "Identify whether the referenced member is static or instance-based.",
                        "Create an object when accessing an instance member.",
                        "Use static only when the member logically belongs to the class.",
                        "Check the member declaration and the calling code."
                ),
                "class Main {\n"
                        + "    int count = 10;\n"
                        + "    public static void main(String[] args) {\n"
                        + "        System.out.println(count);\n"
                        + "    }\n"
                        + "}",
                "class Main {\n"
                        + "    int count = 10;\n"
                        + "    public static void main(String[] args) {\n"
                        + "        Main obj = new Main();\n"
                        + "        System.out.println(obj.count);\n"
                        + "    }\n"
                        + "}"
        ));

    }
}
