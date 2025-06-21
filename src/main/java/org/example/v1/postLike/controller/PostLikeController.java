package org.example.v1.postLike.controller;


import lombok.RequiredArgsConstructor;
import org.example.v1.postLike.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cambooks/post-likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    //좋아요 등록/취소
    @PostMapping
    public ResponseEntity<Void> likeOrUnlike(
            @RequestParam Long memberId,
            @RequestParam Long postId,
            @RequestParam String postType
    ) {
        postLikeService.likePost(postType, postId, memberId);
        return ResponseEntity.ok().build();
    }

    //좋아요 개수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> countLikes(
            @RequestParam Long postId,
            @RequestParam String postType
    ) {
        long count = postLikeService.countLikes(postType, postId);
        return ResponseEntity.ok(count);
    }
}