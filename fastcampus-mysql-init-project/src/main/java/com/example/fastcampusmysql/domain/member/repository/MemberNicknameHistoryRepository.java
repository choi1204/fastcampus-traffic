package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberNicknameHistoryRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TABLE = "MemberNicknameHistory";

    static final RowMapper<MemberNicknameHistory> rowMapper = (ResultSet resultSet, int rowNum) -> MemberNicknameHistory.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .nickname(resultSet.getString("nickName"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public List<MemberNicknameHistory> findAllByMemberId(Long memberId) {
        String sql = String.format("SELECT * FROM %s WHERE memberId = :memberId", TABLE);
        var params = new MapSqlParameterSource().addValue("memberId", memberId);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }
    public MemberNicknameHistory save(MemberNicknameHistory memberNicknameHistory) {
        /**
         * member id를 보고 갱신 또는 삽입을 정한다.
         * 반환값은 id를 담아서 반환.
         */
        if (memberNicknameHistory.getId() == null) {
            return insert(memberNicknameHistory);
        }
        throw new UnsupportedOperationException("");
    }

    private MemberNicknameHistory insert(MemberNicknameHistory memberNicknameHistory) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(memberNicknameHistory);
        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return MemberNicknameHistory.builder()
                .id(id)
                .nickname(memberNicknameHistory.getNickname())
                .memberId(memberNicknameHistory.getMemberId())
                .createdAt(memberNicknameHistory.getCreatedAt())
                .build();
    }

}
