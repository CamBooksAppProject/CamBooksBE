package org.example.v1.comment.controller;

import org.apache.coyote.Response;
import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cambooks/general-forum/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentRequestDto commentRequestDto) {
        String email = userDetails.getUsername();
        CommentResponseDto comment = commentService.createComment(email, commentRequestDto.getPostId(), commentRequestDto);
        return ResponseEntity.ok(comment);
    }
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@RequestParam Long postId) {
        List<CommentResponseDto> commentList = commentService.getCommentList(postId);
        return ResponseEntity.ok(commentList);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countComments(@RequestParam Long postId) {
        return ResponseEntity.ok(commentService.countComment(postId));
    }
    @GetMapping("/my")
    public ResponseEntity<List<CommentResponseDto>> getMyComments(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<CommentResponseDto> commentList = commentService.getMyComment(email);
        return ResponseEntity.ok(commentList);
    }

}
