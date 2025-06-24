package org.example.v1.post.generalForum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralForumPreviewDto {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private Long viewcount;
    private Long postLikeCount;

    public GeneralForumPreviewDto(Long id, String title, String content, String writerName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writerName = writerName;
        this.createdAt = createdAt;
    }
}
