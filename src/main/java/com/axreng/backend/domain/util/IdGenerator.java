package com.axreng.backend.domain.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class IdGenerator {
    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ID_LENGTH = 8;

    public static String generate() {
        return RANDOM.ints(ID_LENGTH, 0, ALPHANUMERIC.length())
                .mapToObj(ALPHANUMERIC::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
} 