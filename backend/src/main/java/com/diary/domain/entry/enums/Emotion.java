package com.diary.domain.entry.enums;

public enum Emotion {
    행복해, 즐거워, 감사해, 사랑해, 뿌듯해, 그저그래, 화나, 힘들어;

    public static Emotion from(String value) {
        for (Emotion e : values()) {
            if (e.name().equalsIgnoreCase(value.trim())) {
                return e;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 감정입니다: " + value);
    }
}
