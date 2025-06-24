package org.example.v1.post.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPreviewDto {
    private Long id;
    private String title;
    private String recruitment;
    private Integer currentParticipants;
    private String thumbnailUrl;
}
