package com.diary.domain.member.dto;

// 내부 응답용 클래스 (간단 메시지 응답)
public  class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
