package com.diary.global.auth;

import com.diary.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public Long getId() {
        return member.getId(); // Member 엔티티에 @Id Long id 있어야 함
    }

    public String getNickname() {
        return member.getNickname(); 
    }

    public Long getMemberId() { return member.getId(); }


    @Override
    public String getUsername() {
        return member.getUserId(); // 로그인 ID 필드
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 없다면 기본적으로 USER 하나만 부여
        return Collections.singleton(() -> "ROLE_USER");
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Member getMember() {
        return member;
    }
}
