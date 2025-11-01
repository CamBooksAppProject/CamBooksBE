package org.example.v1.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private boolean isOwner;
}
