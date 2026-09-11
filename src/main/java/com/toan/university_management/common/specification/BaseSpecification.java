package com.toan.university_management.common.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            return (root, query, cb) -> cb.conjunction();
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
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get(attributeName), value);
    }

    /**
     * Match attribute IN (values) if collection is non-empty
     */
    public static <T> Specification<T> inIfNotEmpty(String attributeName, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> root.get(attributeName).in(values);
    }

    /**
     * Search across multiple attributes matching keyword (OR condition)
     */
    public static <T> Specification<T> keywordSearch(String keyword, String... attributeNames) {
        if (keyword == null || keyword.trim().isEmpty() || attributeNames == null || attributeNames.length == 0) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            for (String attr : attributeNames) {
                if (attr != null && !attr.isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get(attr)), pattern));
                }
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
