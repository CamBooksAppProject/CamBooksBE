package org.example.v1.post.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.community.domain.Region;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPreviewDto {
    private Long id;
    private String title;
    private String recruitment;
    private Integer currentParticipants;
    private Region region;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
}
