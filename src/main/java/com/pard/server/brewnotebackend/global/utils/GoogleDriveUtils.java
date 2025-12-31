package com.pard.server.brewnotebackend.global.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDriveUtils {
    public static String convertToDirectLink(String originalUrl) {

        System.out.println("original Link: " + originalUrl);

        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalStateException("원본 링크가 존재하지 않습니다");
        }

        // 구글 드라이브 공유 링크에서 ID 추출 (예: /file/d/{ID}/view)
        String regex = "/d/([a-zA-Z0-9_-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalUrl);

        if (matcher.find()) {
            String fileId = matcher.group(1);

            // [수정됨] 기존 uc?export=view 대신 차단을 우회하는 thumbnail 주소 사용
            // &sz=w1000 : 이미지 너비를 1000px로 요청 (고화질 유지)
            String resultUrl = "https://drive.google.com/thumbnail?id=" + fileId + "&sz=w1000";

            System.out.println("Converted Link: " + resultUrl);
            return resultUrl;
        }

        // ID를 못 찾은 경우
        throw new IllegalArgumentException("올바른 구글 드라이브 링크가 아닙니다.");
    }
}