package com.axreng.backend.domain.validation;

public class KeywordValidator {
    private static final int MIN_KEYWORD_LENGTH = 4;
    private static final int MAX_KEYWORD_LENGTH = 32;

    public static void validate(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be empty");
        }

        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.length() < MIN_KEYWORD_LENGTH) {
            throw new IllegalArgumentException("Keyword must be at least " + MIN_KEYWORD_LENGTH + " characters long");
        }

        if (trimmedKeyword.length() > MAX_KEYWORD_LENGTH) {
            throw new IllegalArgumentException("Keyword cannot exceed " + MAX_KEYWORD_LENGTH + " characters");
        }

        if (!trimmedKeyword.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Keyword can only contain alphanumeric characters");
        }
    }
} 