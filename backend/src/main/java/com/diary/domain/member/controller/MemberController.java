package com.diary.domain.member.controller;

import com.diary.domain.member.dto.InviteRequest;
import com.diary.domain.member.dto.InviteResponse;
import com.diary.domain.member.dto.MemberResponse;
import com.diary.domain.member.service.MemberInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class MemberController {

    private final MemberInviteService memberInviteService;

    /**
     * 1. 멤버 초대
     */
    @PostMapping("/{diaryId}/invite")
    public ResponseEntity<Map<String, String>> inviteMember(
            @PathVariable Long diaryId,
            @RequestBody InviteRequest request
    ) {
        memberInviteService.inviteMember(diaryId, request);
        return ResponseEntity.ok(Map.of("message", "초대 완료"));
    }

    /**
     * 2. 초대 수락
     */
    @PostMapping("/{diaryId}/accept")
    public ResponseEntity<Map<String, String>> acceptInvite(
            @PathVariable Long diaryId
    ) {
        memberInviteService.acceptInvite(diaryId);
        return ResponseEntity.ok(Map.of("message", "참여 완료"));
    }

    /**
     * 3. 멤버 목록 조회 (수락된 멤버만)
     */
    @GetMapping("/{diaryId}/members")
    public ResponseEntity<List<MemberResponse>> getAcceptedMembers(
            @PathVariable Long diaryId
    ) {
        List<MemberResponse> members = memberInviteService.getAcceptedMembers(diaryId);
        return ResponseEntity.ok(members);
    }

    /**
     * 4. 멤버 추방
     */
    @DeleteMapping("/{diaryId}/members/{userId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable Long diaryId,
            @PathVariable Long userId
    ) {
        memberInviteService.removeMember(diaryId, userId);
        return ResponseEntity.ok(Map.of("message", "멤버 삭제 완료"));
    }

    /**
     * 5. 받은 초대 목록 조회
     */
    @GetMapping("/invites")
    public ResponseEntity<List<InviteResponse>> getMyInvites() {
        List<InviteResponse> invites = memberInviteService.getMyInvites();
        return ResponseEntity.ok(invites);
    }

}
