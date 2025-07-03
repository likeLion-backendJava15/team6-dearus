package com.diary.global.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diary.domain.member.entity.Member;
import com.diary.domain.member.repository.MemberRepository;

@Service
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtAuthProvider jwtProvider; // ✅ JWT 발급기 추가!

	public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
						JwtAuthProvider jwtProvider) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider; // ✅ 주입!
	}

	@Transactional
	public void register(SignupRequest req) {
		if (memberRepository.findByUserId(req.getUserId()).isPresent()) {
			throw new IllegalStateException("이미 존재하는 아이디입니다.");
		}
		Member member = Member.builder().userId(req.getUserId()).password(passwordEncoder.encode(req.getPassword()))
				.nickname(req.getNickname()).email(req.getEmail()).build();
		memberRepository.save(member);
	}

	@Transactional(readOnly = true)
	public String login(String userId, String rawPassword) {
		Member member = memberRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID"));

		if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
			throw new IllegalArgumentException("비밀번호 불일치");
		}

		// ✅ 진짜 JWT 발급
		return jwtProvider.createToken(userId);
	}
}