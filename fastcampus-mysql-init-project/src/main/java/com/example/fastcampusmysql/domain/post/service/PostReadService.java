package com.example.fastcampusmysql.domain.post.service;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostReadService {

    private final PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        return postRepository.groupByCreatedDate(request);
    }

    public Page<Post> getPosts(Long memberId, Pageable pageRequest) {
        return postRepository.findAllByMemberId(memberId, pageRequest);
    }

    public PageCursor<Post> getPosts(Long memberId, CursorRequest cursorRequest) {
        List<Post> posts = findAllBy(memberId, cursorRequest);
        long nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public PageCursor<Post> getPosts(List<Long> memberIdList, CursorRequest cursorRequest) {
        List<Post> posts = findAllBy(memberIdList, cursorRequest);
        long nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    private long getNextKey(List<Post> posts) {
        long nextKey = posts.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
        return nextKey;
    }

    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdAndMemberIdAndOrderByDesc(cursorRequest.key(), memberId, cursorRequest.size());
        }
        return postRepository.findAllByMemberIdAndOrderByDesc(memberId, cursorRequest.size());
    }

    private List<Post> findAllBy(List<Long> memberIdList, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdAndInMemberIdAndOrderByDesc(cursorRequest.key(), memberIdList, cursorRequest.size());
        }
        return postRepository.findAllByInMemberIdAndOrderByDesc(memberIdList, cursorRequest.size());
    }
}
