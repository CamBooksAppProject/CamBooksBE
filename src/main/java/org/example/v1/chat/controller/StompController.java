package org.example.v1.chat.controller;

import org.example.v1.chat.dto.ChatMessageDto;
import org.example.v1.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class StompController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    public StompController(SimpMessageSendingOperations messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }
////    방법 1. MessageMapping(수신)과 SendTo(topic에 메세지 전달) 한번에 처리
//    @MessageMapping("/{roomId}") //클라이언트에서 특정 publish/roomId형태로 메세지 발행 시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId에 메세지를 발행하여 구독중인 클라이언트에 메세지 전송
////    @MessageMapping 어노테이션으로 정의된 WebSocket Controller에게 메세지 전송
//    public String sendMessage(@DestinationVariable Long roomId, String message) {
//        System.out.println(message);
//        return message;
//    }
//
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {
        System.out.println(chatMessageDto.getMessage());
        chatService.beforeSaveMessage(roomId, chatMessageDto);
        messagingTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);
    }


}
