package com.pard.server.brewnotebackend.global.utils;

import java.security.SecureRandom;

public final class PasswordGenerator {

    private static final String CHAR_SET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789";

    private static final int DEFAULT_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {}

    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(idx));
        }

        return sb.toString();
    }
}

