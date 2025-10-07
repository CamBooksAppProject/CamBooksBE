package org.example.v1.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.v1.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.secretKey}")
    private String secretKey;
    private final ChatService chatService;

    public StompHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    private Key getSecretKeySpec() {
        return new SecretKeySpec(
                java.util.Base64.getDecoder().decode(secretKey),
                SignatureAlgorithm.HS512.getJcaName()
        );
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()){
            System.out.println("connect 요청 시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new AuthenticationServiceException("No or invalid Authorization header");
            }
            String token = bearerToken.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKeySpec())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("토큰 검증 완료");
        }

        if(StompCommand.SUBSCRIBE == accessor.getCommand()){
            System.out.println("subscribe 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new AuthenticationServiceException("No or invalid Authorization header");
            }
            String token = bearerToken.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKeySpec())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];
            if(!chatService.isRoomParticipant(email, Long.parseLong(roomId))){
                throw new AuthenticationServiceException("해당 채팅방에 대한 권한이 없습니다.");
            }
        }
        return message;
    }
}
