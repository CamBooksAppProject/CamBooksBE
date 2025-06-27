package org.example.v1.post.generalForum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.comment.dto.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralForumResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private long postLikeCount;
    private List<CommentResponseDto> comments;
    private Long commentsCount;

    public GeneralForumResponseDto(Long id, String title, String content, String writerName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writerName = writerName;
        this.createdAt = createdAt;
    }
}
