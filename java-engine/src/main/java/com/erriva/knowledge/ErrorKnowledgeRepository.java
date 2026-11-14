
package com.erriva.knowledge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores and retrieves reusable Java error knowledge.
 */
public final class ErrorKnowledgeRepository {

    private final Map<String, ErrorKnowledge> entries;

    public ErrorKnowledgeRepository() {
        this.entries = new HashMap<>();
    }

    /**
     * Adds a knowledge entry using its unique error ID.
     */
    public void add(ErrorKnowledge knowledge) {
        Objects.requireNonNull(
                knowledge, "Knowledge cannot be null");

        String id = knowledge.getId();

        if (entries.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Duplicate error ID: " + id);
        }

        entries.put(id, knowledge);
    }

    /**
     * Finds a knowledge entry by its ID.
     */
    public Optional<ErrorKnowledge> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(entries.get(id));
    }

    /**
     * Returns all stored knowledge entries.
     */
    public Collection<ErrorKnowledge> findAll() {
        return Collections.unmodifiableCollection(
                entries.values());
    }

    /**
     * Returns the number of stored entries.
     */
    public int count() {
        return entries.size();
    }
}
