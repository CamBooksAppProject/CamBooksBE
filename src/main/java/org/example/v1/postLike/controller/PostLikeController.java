package org.example.v1.postLike.controller;


import lombok.RequiredArgsConstructor;
import org.example.v1.postLike.domain.PostLike;
import org.example.v1.postLike.domain.PostType;
import org.example.v1.postLike.dto.LikeRequestDto;
import org.example.v1.postLike.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cambooks/post-likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    //좋아요 등록/취소
    @PostMapping
    public boolean likeOrUnlike(@AuthenticationPrincipal UserDetails userDetails, @RequestBody LikeRequestDto likeRequestDto) {
        String email = userDetails.getUsername();
        boolean b = postLikeService.likePost(email, likeRequestDto.getPostType(), likeRequestDto.getPostId());
        return b;
    }

    //좋아요 개수 조회
    @PostMapping("/count")
    public ResponseEntity<Long> countLikes(@RequestBody LikeRequestDto likeRequestDto) {
        long count = postLikeService.countLikes(likeRequestDto.getPostType(), likeRequestDto.getPostId());
        return ResponseEntity.ok(count);
    }
    @GetMapping("/me")
    public ResponseEntity<Map<PostType, List<?>>> getMyLikedPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // Spring Security 기반 인증 가정
        Map<PostType, List<?>> result = postLikeService.getLikedPostPreviews(email);
        return ResponseEntity.ok(result);
    }
}