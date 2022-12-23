package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.service.PostReadService;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTimeLinePostUsecase {

    private final FollowReadService followReadService;

    private final PostReadService postReadService;

    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        List<Follow> followings = followReadService.getFollowers(memberId);
        List<Long> followingMemberIds = followings.stream().map(Follow::getToMemberId).toList();
        return postReadService.getPosts(followingMemberIds, cursorRequest);
    }
}
