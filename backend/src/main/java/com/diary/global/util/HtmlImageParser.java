package com.diary.global.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlImageParser {

    private static final Pattern IMG_TAG_PATTERN = Pattern.compile("<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>");

    public static String extractFirstImageUrl(String htmlContent) {
        if (htmlContent == null) return null;

        Matcher matcher = IMG_TAG_PATTERN.matcher(htmlContent);
        if (matcher.find()) {
            return matcher.group(1);  // 첫 번째 src
        }
        return null;
    }

    public static List<String> extractAllImageUrls(String htmlContent) {
    List<String> urls = new ArrayList<>();
    Matcher matcher = Pattern.compile("<img[^>]+src=['\"]([^'\"]+)['\"]").matcher(htmlContent);
    while (matcher.find()) {
        urls.add(matcher.group(1));
    }
    return urls;
}
}
