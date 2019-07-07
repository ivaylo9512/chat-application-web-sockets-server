package com.chat.app.config;

import com.chat.app.models.UserDetails;
import com.chat.app.security.Jwt;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.List;
import java.util.Map;

public class WebSocketInterceptor implements ChannelInterceptor, HandshakeInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            int id = (int) accessor.getSessionAttributes().get("id");
            List<String> authorization = accessor.getNativeHeader("Authorization");
            UserDetails user = Jwt.validate(authorization.get(0));

            if(user.getId() != id){
                throw new RuntimeException("Corrupted id.");
            }
        }
        return message;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        int mockedId = 5;
        attributes.put("id", mockedId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}