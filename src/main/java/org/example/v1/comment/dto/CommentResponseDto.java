package org.example.v1.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String name;
    private String content;
    private Long count;
    private LocalDateTime createdAt;
    private Long postId;

    public CommentResponseDto(Long id, String name, String content, LocalDateTime createdAt, Long postId) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.createdAt = createdAt;
        this.postId = postId;
    }
}

