package org.example.v1.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChatListResponseDto {
    private Long roomId;
    private String roomName;
    private String isGroupChat;
    private Long unReadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String profileImage;
}
