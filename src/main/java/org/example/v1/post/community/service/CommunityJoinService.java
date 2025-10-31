package org.example.v1.post.community.service;

import jakarta.transaction.Transactional;
import org.example.v1.chat.domain.ChatParticipant;
import org.example.v1.chat.domain.ChatRoom;
import org.example.v1.chat.domain.GroupChatRoomOwner;
import org.example.v1.chat.repository.ChatParticipantRepository;
import org.example.v1.chat.repository.ChatRoomRepository;
import org.example.v1.chat.repository.GroupChatRoomOwnerRepository;
import org.example.v1.chat.service.ChatService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.domain.CommunityJoin;
import org.example.v1.post.community.dto.CommunityJoinResponse;
import org.example.v1.post.community.repository.CommunityJoinRepository;
import org.example.v1.post.community.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunityJoinService {

    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final CommunityJoinRepository communityJoinRepository;
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatParticipantRepository chatParticipantRepository;
    private final GroupChatRoomOwnerRepository groupChatRoomOwnerRepository;
    private final ChatService chatService;

    public CommunityJoinService(CommunityRepository communityRepository,
                                MemberRepository memberRepository,
                                CommunityJoinRepository communityJoinRepository,
                                GroupChatRoomOwnerRepository groupChatRoomOwnerRepository,
                                ChatService chatService) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.communityJoinRepository = communityJoinRepository;
//        this.chatRoomRepository = chatRoomRepository;
//        this.chatParticipantRepository = chatParticipantRepository;
        this.groupChatRoomOwnerRepository = groupChatRoomOwnerRepository;
        this.chatService = chatService;
    }

//    @Transactional
//    public Integer joinAndLeaveCommunity(String email, Long communityId) {
//        Member participant = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
//        Community community = communityRepository.findById(communityId)
//                .orElseThrow(() -> new IllegalArgumentException("커뮤니티가 존재하지 않습니다."));
//        Optional<GroupChatRoomOwner> byOwner = groupChatRoomOwnerRepository.findByOwner(community.getWriter());
//        ChatRoom chatRoom = byOwner.map(GroupChatRoomOwner::getChatRoom).orElse(null);
//        return communityJoinRepository.findByCommunityAndParticipant(community, participant)
//                .map(join -> {
//                    if(chatRoom != null) {
//                        chatService.leaveCommunityChatRoom(chatRoom.getId(), email);
//                    }
//                    communityJoinRepository.delete(join);
//                    community.decrementCurrentParticipants();
//                    communityRepository.save(community);
//                    return community.getCurrentParticipants();
//                })
//                .orElseGet(()->{
//                    if (community.getCurrentParticipants() >= community.getMaxParticipants()) {
//                        throw new IllegalStateException("모집 인원이 가득 찼습니다.");
//                    }
//
//                    chatService.addParticipantToCommunityChat(chatRoom.getId(), email);
//
//                    CommunityJoin join = new CommunityJoin(null, community, participant);
//                    communityJoinRepository.save(join);
//                    community.incrementCurrentParticipants();
//                    communityRepository.save(community);
//                    return community.getCurrentParticipants();
//                });
//    }

    @Transactional
    public CommunityJoinResponse joinAndLeaveCommunity(String email, Long communityId) {
        Member participant = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티가 존재하지 않습니다."));
        Optional<GroupChatRoomOwner> byOwner = groupChatRoomOwnerRepository.findByOwner(community.getWriter());
        ChatRoom chatRoom = byOwner.map(GroupChatRoomOwner::getChatRoom).orElse(null);

        // 이미 참여 중인 경우 → 탈퇴
        return communityJoinRepository.findByCommunityAndParticipant(community, participant)
                .map(join -> {
                    if (chatRoom != null) {
                        chatService.leaveCommunityChatRoom(chatRoom.getId(), email);
                    }
                    communityJoinRepository.delete(join);
                    community.decrementCurrentParticipants();
                    communityRepository.save(community);

                    // 탈퇴 후 상태
                    return CommunityJoinResponse.builder()
                            .currentParticipants(community.getCurrentParticipants())
                            .isJoined(false)
                            .build();
                })
                // 참여하지 않은 경우 → 신규 참가
                .orElseGet(() -> {
                    if (community.getCurrentParticipants() >= community.getMaxParticipants()) {
                        throw new IllegalStateException("모집 인원이 가득 찼습니다.");
                    }

                    if (chatRoom != null) {
                        chatService.addParticipantToCommunityChat(chatRoom.getId(), email);
                    }

                    CommunityJoin join = new CommunityJoin(null, community, participant);
                    communityJoinRepository.save(join);
                    community.incrementCurrentParticipants();
                    communityRepository.save(community);

                    // 참여 후 상태
                    return CommunityJoinResponse.builder()
                            .currentParticipants(community.getCurrentParticipants())
                            .isJoined(true)
                            .build();
                });
    }

}
