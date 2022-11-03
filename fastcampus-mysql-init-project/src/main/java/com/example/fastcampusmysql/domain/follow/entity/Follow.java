package com.example.fastcampusmysql.domain.follow.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 최신의 닉네임을 보여주어야 하므로 정규화 필요
 * 연관된 데이터 방법
 * 1. 조인
 *      - 가능하면 미루는게 좋다
 *      - JOIN 시 SERVICE 레이어에 결합이 생겨서 유연한 아키텍처가 되기 힘들다.
 *      - 결합 문제에 리팩토링 힘듬.
 * 2. 추가 쿼리
 * 3. 별도의 저장소
 */
@Getter
public class Follow {

    private final Long id;

    private final Long fromMemberId;

    private final Long toMemberId;

    private final LocalDateTime createdAt;

    @Builder
    public Follow(Long id, Long fromMemberId, Long toMemberId, LocalDateTime createdAt) {
        this.id = id;
        this.fromMemberId = Objects.requireNonNull(fromMemberId);
        this.toMemberId = Objects.requireNonNull(toMemberId);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
