package org.example.v1.post.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityJoinResponse {
    private Integer currentParticipants;
    private Boolean isJoined;
}
