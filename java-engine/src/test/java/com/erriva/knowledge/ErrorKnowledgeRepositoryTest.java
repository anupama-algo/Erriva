
package com.erriva.knowledge;

import com.erriva.model.ErrorCategory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorKnowledgeRepositoryTest {

    private ErrorKnowledge createSampleKnowledge(String id) {
        return new ErrorKnowledge(
                id,
                ErrorCategory.RUNTIME,
                "Sample Error",
                "A sample explanation.",
                List.of("Sample cause"),
                List.of("Sample debugging step"),
                "int x = 1 / 0;",
                "int x = 1;"
        );
    }

    @Test
    void registersFiveRuntimeErrors() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        RuntimeErrorKnowledge.registerAll(repository);

        assertEquals(5, repository.count());
        assertTrue(repository.findById(
                "RUNTIME_NULL_POINTER").isPresent());
        assertTrue(repository.findById(
                "RUNTIME_ARRAY_INDEX").isPresent());
        assertTrue(repository.findById(
                "RUNTIME_ARITHMETIC").isPresent());
        assertTrue(repository.findById(
                "RUNTIME_NUMBER_FORMAT").isPresent());
        assertTrue(repository.findById(
                "RUNTIME_CLASS_CAST").isPresent());
    }

    @Test
    void registersFiveCompilerErrors() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        CompilerErrorKnowledge.registerAll(repository);

        assertEquals(5, repository.count());
        assertTrue(repository.findById(
                "COMPILER_CANNOT_FIND_SYMBOL").isPresent());
        assertTrue(repository.findById(
                "COMPILER_INCOMPATIBLE_TYPES").isPresent());
        assertTrue(repository.findById(
                "COMPILER_SEMICOLON_EXPECTED").isPresent());
        assertTrue(repository.findById(
                "COMPILER_MISSING_RETURN").isPresent());
        assertTrue(repository.findById(
                "COMPILER_UNINITIALIZED_VARIABLE").isPresent());
    }

    @Test
    void registersTenErrorsTogether() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        RuntimeErrorKnowledge.registerAll(repository);
        CompilerErrorKnowledge.registerAll(repository);

        assertEquals(10, repository.count());
        assertEquals(10, repository.findAll().size());
    }

    @Test
    void findsKnowledgeById() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        repository.add(createSampleKnowledge("TEST_ERROR"));

        ErrorKnowledge result =
                repository.findById("TEST_ERROR").orElseThrow();

        assertEquals("TEST_ERROR", result.getId());
        assertEquals("Sample Error", result.getTitle());
        assertEquals(ErrorCategory.RUNTIME, result.getCategory());
    }

    @Test
    void returnsEmptyForUnknownId() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        assertTrue(repository.findById(
                "UNKNOWN_ERROR").isEmpty());
    }

    @Test
    void returnsEmptyForNullId() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        assertTrue(repository.findById(null).isEmpty());
    }

    @Test
    void returnsEmptyForBlankId() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        assertTrue(repository.findById("   ").isEmpty());
    }

    @Test
    void rejectsDuplicateIds() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        repository.add(createSampleKnowledge("DUPLICATE"));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(
                        createSampleKnowledge("DUPLICATE"))
        );

        assertEquals(1, repository.count());
    }

    @Test
    void rejectsNullKnowledge() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        assertThrows(
                NullPointerException.class,
                () -> repository.add(null)
        );
    }

    @Test
    void returnsReadOnlyCollection() {
        ErrorKnowledgeRepository repository =
                new ErrorKnowledgeRepository();

        repository.add(createSampleKnowledge("TEST_ERROR"));

        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.findAll().clear()
        );

        assertEquals(1, repository.count());
    }

    @Test
    void rejectsBlankKnowledgeId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createSampleKnowledge("   ")
        );
    }

    @Test
    void protectsKnowledgeListsFromChanges() {
        List<String> causes = new ArrayList<>();
        causes.add("Original cause");

        ErrorKnowledge knowledge = new ErrorKnowledge(
                "IMMUTABLE_TEST",
                ErrorCategory.RUNTIME,
                "Test Error",
                "Test explanation",
                causes,
                List.of("Debugging step"),
                "Incorrect example",
                "Corrected example"
        );

        causes.add("New cause");

        assertEquals(1, knowledge.getPossibleCauses().size());
        assertEquals(
                "Original cause",
                knowledge.getPossibleCauses().get(0)
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> knowledge.getPossibleCauses().add(
                        "Another cause")
        );
    }
}
