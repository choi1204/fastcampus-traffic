package com.example.fastcampusmysql.domain.member.entity;

import com.example.fastcampusmysql.util.MemberFixtureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @DisplayName("회원은 닉네임을 변경할 수 있다.")
    @Test
    public void testChangeNickName() {
        Member member = MemberFixtureFactory.create(1L);
        String expect = "choi";

        member.changeNickname(expect);

        assertEquals(expect, member.getNickname());
    }

    @DisplayName("회원의 닉네임은 10자를 초과할 수 없다.")
    @Test
    public void testNicknameMaxLength() {
        Member member = MemberFixtureFactory.create(1L);
        String overMaxLengthName = "choi1234567890";

        assertThrows(
                IllegalArgumentException.class,
                () -> member.changeNickname(overMaxLengthName)
        );
    }
}