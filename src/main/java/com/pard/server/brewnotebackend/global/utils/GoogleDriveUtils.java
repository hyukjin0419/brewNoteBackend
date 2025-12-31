package com.pard.server.brewnotebackend.global.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDriveUtils {
    public static String convertToDirectLink(String originalUrl) {

        //null이거나 빈 값이면 에러 대신 null 반환
        if (originalUrl == null || originalUrl.isBlank()) {
            return null;
        }

        System.out.println("original Link: " + originalUrl);

        // 구글 드라이브 공유 링크에서 ID 추출
        String regex = "/d/([a-zA-Z0-9_-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalUrl);

        if (matcher.find()) {
            String fileId = matcher.group(1);
            String resultUrl = "https://drive.google.com/thumbnail?id=" + fileId + "&sz=w1000";
            System.out.println("Converted Link: " + resultUrl);
            return resultUrl;
        }

        throw new IllegalArgumentException("올바른 구글 드라이브 링크가 아닙니다.");
    }
}