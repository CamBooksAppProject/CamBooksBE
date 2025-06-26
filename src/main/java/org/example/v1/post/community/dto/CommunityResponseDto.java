package org.example.v1.post.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.community.domain.Region;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponseDto {
    private Long id;
    private String title;
    private Region region;
    private String recruitment;  // 모집공고
    private String introduction; // 동아리 소개
    private Integer currentParticipants;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> imgUrls;

    public CommunityResponseDto(Long id, String title, Region region, String recruitment, String introduction, Integer currentParticipants, Integer maxParticipants,LocalDateTime createdAt ,LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.region = region;
        this.recruitment = recruitment;
        this.introduction = introduction;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.createdAt = createdAt;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}

