package org.example.v1.comment.controller;

import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.comment.service.CommentService;
import org.example.v1.comment.service.CommunityCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/cambooks/community/comment")

public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;

    public CommunityCommentController(CommunityCommentService communityCommentService) {
        this.communityCommentService = communityCommentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentRequestDto commentRequestDto) {
        String email = userDetails.getUsername();
        CommentResponseDto comment = communityCommentService.createComment(email, commentRequestDto.getPostId(), commentRequestDto);
        return ResponseEntity.ok(comment);
    }
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@RequestParam Long postId) {
        List<CommentResponseDto> commentList = communityCommentService.getCommentList(postId);
        return ResponseEntity.ok(commentList);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countComments(@RequestParam Long postId) {
        return ResponseEntity.ok(communityCommentService.countComment(postId));
    }
    @GetMapping("/my")
    public ResponseEntity<List<CommentResponseDto>> getMyComments(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<CommentResponseDto> commentList = communityCommentService.getMyComment(email);
        return ResponseEntity.ok(commentList);
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long commentId) {
        String email = userDetails.getUsername();
        communityCommentService.deleteComment(email, commentId);
        return ResponseEntity.ok().build();
    }
}