package org.example.v1.post.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.community.domain.Region;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRequestDto {
    private String title;
    private Region region;
    private String recruitment;
    private String introduction;
    private Integer maxParticipants;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
