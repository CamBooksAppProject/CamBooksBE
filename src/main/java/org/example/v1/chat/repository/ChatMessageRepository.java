package org.example.v1.chat.repository;

import org.example.v1.chat.domain.ChatMessage;
import org.example.v1.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomAndUpdatedTimeAfterOrderByCreatedTimeAsc(
            ChatRoom chatRoom,
            LocalDateTime updatedTime
    );
}
