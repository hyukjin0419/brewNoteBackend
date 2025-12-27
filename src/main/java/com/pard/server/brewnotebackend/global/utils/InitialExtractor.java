package com.pard.server.brewnotebackend.global.utils;

public class InitialExtractor {
    private static final char[] initial = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };


    public static String getInitial(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 0xAC00 && c <= 0xD7A3) {
                int initialIndex = (c - 0xAC00) / 28 / 21;
                sb.append(initial[initialIndex]);
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
