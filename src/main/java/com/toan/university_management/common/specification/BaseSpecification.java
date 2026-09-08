package com.toan.university_management.common.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

/**
 * BaseSpecification utility provides reusable JPA Criteria Specifications
 * for dynamic filtering directly at the database level.
 */
public final class BaseSpecification {

    private BaseSpecification() {
        // Utility class
    }

    /**
     * Filter active records (soft-delete flag deleted = false)
     */
    public static <T> Specification<T> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /**
     * Case-insensitive LIKE search (%value%) on a string attribute
     */
    public static <T> Specification<T> likeIgnoreCase(String attributeName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.like(
                cb.lower(root.get(attributeName)),
                "%" + value.trim().toLowerCase() + "%"
        );
    }

    /**
     * Exact match for attribute = value if value is non-null
     */
    public static <T> Specification<T> equalsIfNotNull(String attributeName, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(attributeName), value);
    }

    /**
     * Match attribute IN (values) if collection is non-empty
     */
    public static <T> Specification<T> inIfNotEmpty(String attributeName, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get(attributeName).in(values);
    }
}
