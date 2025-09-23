package org.example.v1.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    public StompWebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
//                ws://가 아닌 http:// 엔드포인트 사용할 수 있게 해주는 sockjs라이브러리를 통해 요청을 허용하는 설정
                .withSockJS();
    }
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//    /publish/1형태로 메세지를 발행해야 함을 설정
//        /publish로 시작하는 url패턴으로 메세지가 발행되면, @Controller 객체의 @MessaMapping메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/publish");
//    /topic/1형태로 메세지를 수신(subscribe)해야 함을 설정
        registry.enableSimpleBroker("/topic");
    }

//    웹소켓 요청( connect, subscirbe, disconnect) 요청 시에는 http header등 http메세지를 넣어올 수 있고, 이를 interceptor를 통해 가로채 토큰 등을 검증 가능.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
