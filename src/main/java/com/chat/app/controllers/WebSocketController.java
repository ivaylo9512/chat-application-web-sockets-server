package com.chat.app.controllers;

import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.security.Jwt;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
public class WebSocketController {
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;
    private UserService userService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }


    @MessageMapping("/message")
    public void message(Principal principal, MessageDto message, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUser;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUser = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }
        message.setSenderId(loggedUser.getId());
        chatService.addNewMessage(message);

        messagingTemplate.convertAndSendToUser(message.getUsername(), "/message", message);
    }

    @MessageMapping("/createChat")
    @Transactional
    public void createChat(Principal principal, int userId, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUserDetails;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUserDetails = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }

        UserModel user = userService.findById(userId);
        UserModel loggedUser = userService.findById(loggedUserDetails.getId());

        chatService.createChat(loggedUser, user);
        messagingTemplate.convertAndSendToUser(user.getUsername(), "/message", "creatingChat");
    }
}
