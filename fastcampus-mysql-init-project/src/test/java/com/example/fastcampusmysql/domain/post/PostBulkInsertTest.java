package com.example.fastcampusmysql.domain.post;

import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import com.example.fastcampusmysql.util.PostFixtureFactory;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 가장 원초적으로 작성.
 * 부하테스트 다른 방법 학습 필요
 * 테스트코드와 같이 돌면 안되는 코드이므로 테스트코드 구동시 따로 빠지게할 수 있을지 고민하면 좋음.
 */

@SpringBootTest
public class PostBulkInsertTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void bulkInsert() {
        EasyRandom easyRandom = PostFixtureFactory.get(3L,
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 2, 1)
        );

        StopWatch stopWatch = new StopWatch();
        StopWatch queryStopWatch = new StopWatch();

        stopWatch.start();
        List<Post> posts = IntStream.range(0, 10000 * 100)
                .parallel()
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();

        stopWatch.stop();
        System.out.println("객체 생성 시간 : " + stopWatch.getTotalTimeSeconds());

        queryStopWatch.start();
        postRepository.bulkInsert(posts);
        queryStopWatch.stop();
        System.out.println("쿼리 실행 시간 : " + queryStopWatch.getTotalTimeSeconds());
    }
}
