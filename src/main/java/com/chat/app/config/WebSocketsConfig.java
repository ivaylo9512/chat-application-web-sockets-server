package com.chat.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketsConfig
        implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/message", "/createChat", "/user");
        config.setApplicationDestinationPrefixes("/api");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/chat/message")
                .setAllowedOrigins("*")
                .addInterceptors(getWebSocketInterceptor());
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(getWebSocketInterceptor());
    }
    private WebSocketInterceptor getWebSocketInterceptor(){
        return new WebSocketInterceptor();
    }
}
