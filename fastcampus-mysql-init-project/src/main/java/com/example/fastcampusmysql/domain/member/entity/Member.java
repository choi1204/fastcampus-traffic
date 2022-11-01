package com.example.fastcampusmysql.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Member {

    private final Long id;

    private String nickname;

    private final String email;

    private final LocalDate birthday;

    private final LocalDateTime createdAt;

    private static final Long NAME_MAX_LENGTH = 10L;

    @Builder
    private Member(Long id, String nickname, String email, LocalDate birthday, LocalDateTime createdAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.birthday = Objects.requireNonNull(birthday);

        validateNickname(nickname);
        this.nickname = Objects.requireNonNull(nickname);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    private void validateNickname(String nickname) {
        // 커스텀 Exception으로 변경 추천(강의 여서 속도를 위해)
        Assert.isTrue(nickname.length() <= NAME_MAX_LENGTH.intValue(), "최대 길이를 초과했습니다.");
    }

    public void changeNickname(String to) {
        Objects.requireNonNull(to);
        validateNickname(to);
        nickname = to;
    }
}
