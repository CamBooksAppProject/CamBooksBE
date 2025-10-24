package org.example.v1.chat.controller;

import org.example.v1.chat.dto.ChatMessageDto;
import org.example.v1.chat.dto.ChatRoomListResponseDto;
import org.example.v1.chat.dto.MyChatListResponseDto;
import org.example.v1.chat.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cambooks/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

//    그룹채팅방 개설
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam String roomName){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }

//    그룹채팅목록조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms(){
        List<ChatRoomListResponseDto> chatRooms = chatService.getGroupChatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

//    그룹채팅방 참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId){
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }

    ////    1대1 채팅방 개설 또는 기존 채팅방 roomId return
    @PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateChatRoom(@RequestParam Long otherMemberId){
        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    ////    기존 채팅방 메세지 가져오기
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId){
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

    ////    채팅 메세지 읽음 처리
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@PathVariable Long roomId){
        chatService.messageRead(roomId);
        return ResponseEntity.ok().build();
    }

    ////    내 채팅방 목록조회 : roomId, roomName, 그룹채팅 여부, 메새지 읽음 개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(){
        List<MyChatListResponseDto> myChatListResponseDtos = chatService.getMyChatRooms();
        return new ResponseEntity<>(myChatListResponseDtos, HttpStatus.OK);
    }

    ////   1대1 채팅방 떠나기(양측 모두 떠나면 채팅방 삭제)
    @DeleteMapping("/room/{roomId}/leave")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long roomId){
        chatService.leaveChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

    ////    그룹(커뮤니티) 채팅방 떠나기(모든 멤버 떠나면 or 방장 떠나면 방 삭제)
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatRoom(@PathVariable Long roomId){
        chatService.leaveGroupChatRoom(roomId);
        return ResponseEntity.ok().build();
    }
}
