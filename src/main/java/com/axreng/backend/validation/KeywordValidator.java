package com.axreng.backend.validation;

public class KeywordValidator {
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 32;

    public static void validate(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be empty");
        }

        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Keyword must have at least " + MIN_LENGTH + " characters");
        }

        if (trimmedKeyword.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Keyword must have at most " + MAX_LENGTH + " characters");
        }
    }
} 