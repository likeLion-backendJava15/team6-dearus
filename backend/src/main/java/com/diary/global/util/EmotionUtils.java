package com.diary.global.util;

import com.diary.domain.entry.enums.Emotion;

public class EmotionUtils {

    public static String toEmoji(Emotion emotion) {
        if (emotion == null) return "";

        return switch (emotion) {
            case 행복해 -> "😊";
            case 즐거워 -> "😄";
            case 감사해 -> "🙏";
            case 사랑해 -> "❤️";
            case 뿌듯해 -> "😌";
            case 그저그래 -> "😐";
            case 화나 -> "😡";
            case 힘들어 -> "😫";
        };
    }
}
