package org.example.v1.chat.repository;

import org.example.v1.chat.domain.ChatRoom;
import org.example.v1.chat.domain.GroupChatRoomOwner;
import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupChatRoomOwnerRepository extends JpaRepository<GroupChatRoomOwner, Long> {
    Optional<GroupChatRoomOwner> findByOwner(Member owner);
    Optional<GroupChatRoomOwner> findByChatRoom(ChatRoom chatRoom);
    Optional<GroupChatRoomOwner> findByCommunity(Community community);
}
