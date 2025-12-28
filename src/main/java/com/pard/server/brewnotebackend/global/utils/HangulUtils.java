package com.pard.server.brewnotebackend.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HangulUtils {

    private static final char HANGUL_BASE = 0xAC00;
    private static final char HANGUL_END = 0xD7A3;

    private static final char[] initial = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
            'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public static boolean isHangulSyllable(char c) {
        return c >= HANGUL_BASE && c <= HANGUL_END;
    }

    public static boolean isInitial(char c) {
        for (char i : initial) {
            if (i == c) return true;
        }
        return false;
    }

    public static char extractInitial(char syllable) {
        int index = (syllable - HANGUL_BASE) / (21 * 28);
        return initial[index];
    }

    //입력 문자열을 "초성 시퀀스"로 변환
    /*
    아ㅁㄹ -> ㅇㅁㄹ
    ㅇ메 -> ㅇㅁ
    아멜 -> ㅇㅁㄹ
    아메리ㄱ -> ㅇㅁㄹㄱ
     */
    public static String extractInitialSequence(String input) {
        StringBuilder sb = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (isHangulSyllable(c)) {
                sb.append(extractInitial(c));
            } else if (isInitial(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //완성형 prefix 추출
    /*
    아ㅁㄹ -> 아
    ㅇ메 -> 메
     */
    public static String extractHangulPrefix(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if(isHangulSyllable(c)){
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
