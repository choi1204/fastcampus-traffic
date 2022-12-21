package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.util.PageHelper;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@RequiredArgsConstructor
@Repository
public class PostRepository {
    static final String TABLE = "Post";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<DailyPostCount> DAILY_POST_COUNT_MAPPER = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
            resultSet.getLong("memberId"),
            resultSet.getObject("createdDate", LocalDate.class),
            resultSet.getLong("count")
    );

    private static final RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
//            .likeCount(resultSet.getLong("likeCount"))
//            .version(resultSet.getLong("version"))
            .build();

    public Post save(Post post) {
        if (post.getId() == null)
            return insert(post);
        return update(post);
    }

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        var sql = String.format("""
                SELECT createdDate, memberId, count(id)
                FROM %s
                WHERE memberId = :memberId AND createdDate between :firstDate and :lastDate
                GROUP BY createdDate, memberId
                """, TABLE);
        var params = new BeanPropertySqlParameterSource(request);
        return namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_MAPPER);
    }

    private Post insert(Post post) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        var id = jdbcInsert.executeAndReturnKey(params).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }


    private Post update(Post post) {
        var sql = String.format("""
                UPDATE %s set 
                    memberId = :memberId, 
                    contents = :contents, 
                    createdDate = :createdDate, 
                    createdAt = :createdAt, 
                    likeCount = :likeCount,
                    version = :version + 1 
                WHERE id = :id and version = :version
                """, TABLE);

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        var updatedCount = namedParameterJdbcTemplate.update(sql, params);
        if (updatedCount == 0) {
            throw new RuntimeException("not updated");
        }
        return post;
    }

    public void bulkInsert(List<Post> posts) {
        var sql = String.format("""
                INSERT INTO `%s` (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """, TABLE);

        SqlParameterSource[] params = posts
                .stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    /*
    일반적으로 Sort를 파라미터로 받지 않는다.(인덱스를 탈지 안탈지 몰라서)
    동적쿼리로 Sort를 탈경우 일반적으로 MySQL이 아닌 다른 DB를 사용.
     */
    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        var sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY $s
                LIMIT :size
                OFFSET :offset
                 """, TABLE, PageHelper.orderBy(pageable.getSort()));
        List<Post> posts = namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
        return new PageImpl(posts, pageable, getCount(memberId));
    }

    public List<Post> findAllByMemberIdAndOrderByDesc(Long memberId, int size) {
        var sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id desc
                LIMIT :size
                """, TABLE);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size)
                ;
        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdAndOrderByDesc(Long id, Long memberId, int size) {
        var sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id desc
                LIMIT :size
                """, TABLE);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size)
                .addValue("id", id);
                ;
        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }


    private Long getCount(Long memberId) {
        var sql = String.format("""
                SELECT count(id)
                FROM %s
                WHERE memberId = :memberId
                """, TABLE);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }
}

