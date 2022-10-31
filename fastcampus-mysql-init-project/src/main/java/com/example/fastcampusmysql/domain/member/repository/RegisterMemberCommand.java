package com.example.fastcampusmysql.domain.member.repository;

import java.time.LocalDate;

public record RegisterMemberCommand(
        String email,
        String nickname,
        LocalDate birthdate
) {
}
