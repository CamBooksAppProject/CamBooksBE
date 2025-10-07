package org.example.v1.chat.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.v1.chat.domain.ChatMessage;
import org.example.v1.chat.domain.ChatParticipant;
import org.example.v1.chat.domain.ChatRoom;
import org.example.v1.chat.domain.ReadStatus;
import org.example.v1.chat.dto.ChatMessageDto;
import org.example.v1.chat.dto.ChatRoomListResponseDto;
import org.example.v1.chat.dto.MyChatListResponseDto;
import org.example.v1.chat.repository.ChatMessageRepository;
import org.example.v1.chat.repository.ChatParticipantRepository;
import org.example.v1.chat.repository.ChatRoomRepository;
import org.example.v1.chat.repository.ReadStatusRepository;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ChatParticipantRepository chatParticipantRepository, ReadStatusRepository readStatusRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.readStatusRepository = readStatusRepository;
        this.memberRepository = memberRepository;
    }
    public void beforeSaveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        //        채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
//        보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c: chatParticipants) {
            if (c.isLeft()) {
                c.setIsLeft(false);
                System.out.println("-----텟트ㅡ");
                chatParticipantRepository.save(c);
            }
        }
        this.saveMessage(roomId, chatMessageDto);
    }

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
//        채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
//        보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        //        메세지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);
//        사용자별로 읽음 여부 저장
        for(ChatParticipant c: chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    public void createGroupRoom(String chatRoomName){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅 참여자로 개설자를 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomListResponseDto> getGroupChatRooms(){
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResponseDto> dtos = new ArrayList<>();
        for(ChatRoom chatRoom: chatRooms){
            ChatRoomListResponseDto dto = ChatRoomListResponseDto.builder()
                    .roomId(chatRoom.getId())
                    .roomName(chatRoom.getName())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void addParticipantToGroupChat(Long roomId){
//        채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
//        member 조회
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
        if(chatRoom.getIsGroupChat().equals("N"))
            throw new IllegalArgumentException("채팅 참여 권한이 없습니다.");
//        이미 참여자인지 검증
        Optional<ChatParticipant> participant =  chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, member);
        }



//        ChatParticipants 객체 생성 후 저장
    }
    public void addParticipantToRoom(ChatRoom chatRoom, Member member){
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId){
//내가 해당 채팅방의 참여자가 아닌 경우 에러
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        ChatParticipant myParticipant = null;
        boolean check = false;
        for(ChatParticipant c: chatParticipants){
            if(c.getMember().equals(member)){
                myParticipant = c;
                check = true;
            }
        }
        if(!check)throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
//        특정 room에 대한 message 조회
//        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessage> messages = this.getMessagesAfterParticipantUpdate(myParticipant);
        List<ChatMessageDto> dtos = new ArrayList<>();
        for(ChatMessage m: messages){
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .message(m.getContent())
                    .senderEmail(m.getMember().getEmail())
                    .build();
            dtos.add(chatMessageDto);
        }
        return dtos;
    }

    public List<ChatMessage> getMessagesAfterParticipantUpdate(ChatParticipant participant) {
        return chatMessageRepository.findByChatRoomAndUpdatedTimeAfterOrderByCreatedTimeAsc(
                participant.getChatRoom(),
                participant.getUpdatedTime()
        );
    }

    public boolean isRoomParticipant(String email, Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c: chatParticipants){
            if(c.getMember().equals(member)){
                return true;
            }
        }
        return false;
    }

    public void messageRead(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for(ReadStatus r: readStatuses){
            r.updateIsRead(true);
        }
    }
    public List<MyChatListResponseDto> getMyChatRooms() {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResponseDto> dtos = new ArrayList<>();

        for (ChatParticipant c : chatParticipants) {
            // 내가 나간 방인데, 읽지 않은 메시지가 없으면 → continue (리스트에 포함 X)
            if (c.isLeft()) {
                Long unreadCount = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
                if (unreadCount == 0) {
                    continue; // 이 방은 내 목록에서 제외
                }
            }

            Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);

            // 마지막 메시지 조회
            ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedTimeDesc(c.getChatRoom());
            String lastMessageContent = (lastMessage != null) ? lastMessage.getContent() : "";

            if (c.getChatRoom().getIsGroupChat().equals("N")) {
                // 1:1 방 → 상대방 찾기
                Member otherMember = c.getChatRoom().getChatParticipants().stream()
                        .map(ChatParticipant::getMember)
                        .filter(m -> !m.equals(member))
                        .findFirst()
                        .orElse(null);

                MyChatListResponseDto dto = MyChatListResponseDto.builder()
                        .roomId(c.getChatRoom().getId())
                        .roomName(otherMember != null ? otherMember.getNickname() : "Unknown")
                        .isGroupChat(c.getChatRoom().getIsGroupChat())
                        .unReadCount(count)
                        .lastMessage(lastMessageContent)
                        .lastMessageTime(lastMessage != null ? lastMessage.getCreatedTime() : null)
                        .build();

                dtos.add(dto);

            } else {
                // 그룹채팅
                MyChatListResponseDto dto = MyChatListResponseDto.builder()
                        .roomId(c.getChatRoom().getId())
                        .roomName(c.getChatRoom().getName())
                        .isGroupChat(c.getChatRoom().getIsGroupChat())
                        .unReadCount(count)
                        .lastMessage(lastMessageContent)
                        .lastMessageTime(lastMessage != null ? lastMessage.getCreatedTime() : null)
                        .build();

                dtos.add(dto);
            }
        }
        
        // 최신 메시지 시간 순으로 정렬 (최근 메시지가 먼저)
        dtos.sort((dto1, dto2) -> {
            if (dto1.getLastMessageTime() == null && dto2.getLastMessageTime() == null) return 0;
            if (dto1.getLastMessageTime() == null) return 1;
            if (dto2.getLastMessageTime() == null) return -1;
            return dto2.getLastMessageTime().compareTo(dto1.getLastMessageTime());
        });
        
        return dtos;
    }


    public void leaveGroupChatRoom(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member not found"));
//        if(chatRoom.getIsGroupChat().equals("N")){
//            throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
//        }
        ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(()-> new EntityNotFoundException("Participant not found"));
        c.setIsLeft(true);
//        chatParticipantRepository.delete(c);
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        int left = 1;
        for(ChatParticipant p: chatParticipants){
            if(!p.isLeft())
                left *= 0;
        }
        if(left == 1){
            chatRoomRepository.delete(chatRoom);
        }
//        if(chatParticipants.isEmpty()){
//            chatRoomRepository.delete(chatRoom);
//        }
    }

    public Long getOrCreatePrivateRoom(Long otherMemberId){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("Member1 not found"));
        Member otherMember = memberRepository.findById(otherMemberId)
                .orElseThrow(()-> new EntityNotFoundException("Member2 not found"));

//        나와 상대방이 1대1 채팅에 이미 참여하고 있다면, 해당 roomId return
        Optional<ChatRoom> chatRoom = chatParticipantRepository.findExistingPrivateRoom(member.getId(), otherMember.getId());
        if(chatRoom.isPresent()){
            ChatRoom existingRoom = chatRoom.get();
            ChatParticipant participant = chatParticipantRepository.findByChatRoomAndMember(existingRoom, member)
                    .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

            // ⭐ 이미 존재하는 채팅방이면 isLeft를 false로 되돌립니다.
            if (participant.isLeft()) {
                participant.setIsLeft(false);
                chatParticipantRepository.save(participant);
            }
            return existingRoom.getId();
        }
        if(member.equals(otherMember)){
            throw new IllegalArgumentException("자기 자신과의 채팅은 불가능합니다.");
        }
//        만약, 1대1 채팅방이 없을 경우 신규 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .isGroupChat("N")
                .name(otherMember.getNickname() + "님과 "+ member.getNickname() +"님의 채팅방")
                .build();
        chatRoomRepository.save(newRoom);
//        두 사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newRoom, member);
        addParticipantToRoom(newRoom, otherMember);

        return newRoom.getId();
    }
}
