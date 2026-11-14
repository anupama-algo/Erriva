
package com.erriva.knowledge;

import com.erriva.model.ErrorCategory;
import java.util.List;

/**
 * Registers predefined Java runtime error knowledge.
 */
public final class RuntimeErrorKnowledge {

    private RuntimeErrorKnowledge() {
        // Utility class: objects are not required.
    }

    public static void registerAll(
            ErrorKnowledgeRepository repository) {

        repository.add(new ErrorKnowledge(
                "RUNTIME_NULL_POINTER",
                ErrorCategory.RUNTIME,
                "NullPointerException",
                "An operation was attempted on a null reference.",
                List.of(
                        "An object was never initialized.",
                        "A method returned null.",
                        "A variable was assigned null."
                ),
                List.of(
                        "Find the line mentioned in the stack trace.",
                        "Identify the reference being used.",
                        "Check where that reference receives its value.",
                        "Initialize the object or handle null safely."
                ),
                "String name = null;\n"
                        + "System.out.println(name.length());",
                "String name = \"Erriva\";\n"
                        + "System.out.println(name.length());"
        ));

        repository.add(new ErrorKnowledge(
                "RUNTIME_ARRAY_INDEX",
                ErrorCategory.RUNTIME,
                "ArrayIndexOutOfBoundsException",
                "An array was accessed using an invalid index.",
                List.of(
                        "The index is negative.",
                        "The index is equal to or greater than the array length.",
                        "A loop uses an incorrect boundary."
                ),
                List.of(
                        "Check the invalid index in the error message.",
                        "Check the array length.",
                        "Remember that array indexes start at zero.",
                        "Use index < array.length in loops."
                ),
                "int[] nums = {10, 20, 30};\n"
                        + "System.out.println(nums[3]);",
                "int[] nums = {10, 20, 30};\n"
                        + "System.out.println(nums[2]);"
        ));

        repository.add(new ErrorKnowledge(
                "RUNTIME_ARITHMETIC",
                ErrorCategory.RUNTIME,
                "ArithmeticException",
                "An arithmetic operation could not be completed.",
                List.of(
                        "Integer division by zero.",
                        "An integer remainder operation uses zero."
                ),
                List.of(
                        "Locate the arithmetic expression.",
                        "Check the divisor before performing the operation.",
                        "Handle zero as a special case."
                ),
                "int result = 10 / 0;",
                "int divisor = 2;\n"
                        + "if (divisor != 0) {\n"
                        + "    int result = 10 / divisor;\n"
                        + "}"
        ));

        repository.add(new ErrorKnowledge(
                "RUNTIME_NUMBER_FORMAT",
                ErrorCategory.RUNTIME,
                "NumberFormatException",
                "A string could not be converted into the requested number.",
                List.of(
                        "The string contains nonnumeric characters.",
                        "The string is empty.",
                        "The number is outside the supported range."
                ),
                List.of(
                        "Inspect the string being converted.",
                        "Check that it matches the expected number format.",
                        "Use try-catch when input may be invalid."
                ),
                "int age = Integer.parseInt(\"twenty\");",
                "try {\n"
                        + "    int age = Integer.parseInt(\"20\");\n"
                        + "} catch (NumberFormatException e) {\n"
                        + "    System.out.println(\"Invalid number\");\n"
                        + "}"
        ));

        repository.add(new ErrorKnowledge(
                "RUNTIME_CLASS_CAST",
                ErrorCategory.RUNTIME,
                "ClassCastException",
                "An object was cast to an incompatible type.",
                List.of(
                        "The object's actual type differs from the target type.",
                        "An incorrect assumption was made about inheritance."
                ),
                List.of(
                        "Check the object's actual runtime type.",
                        "Verify the intended target type.",
                        "Use instanceof when a cast may be unsafe."
                ),
                "Object value = \"Hello\";\n"
                        + "Integer number = (Integer) value;",
                "Object value = \"Hello\";\n"
                        + "if (value instanceof String text) {\n"
                        + "    System.out.println(text);\n"
                        + "}"
        ));
    }
}
