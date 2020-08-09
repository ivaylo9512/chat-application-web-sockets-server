package com.chat.app.controllers;

import com.chat.app.models.DTOs.ChatDto;
import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.Session;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.chat.app.security.Jwt;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/chat/auth")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private SimpMessagingTemplate messagingTemplate;


    public ChatController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/getChats")
    public List<ChatDto> getChats(@RequestParam(name = "pageSize") int pageSize){
        UserDetails userDetails = (UserDetails)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        int userId = userDetails.getId();

        return chatService.getUserChats(userId, pageSize);

    }

    @GetMapping(value = "/nextSessions")
    public List<Session> getChatSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        return chatService.getChatSessions(chatId, page, pageSize);
    }

    @PostMapping("/create")
    public ChatDto createChat(@RequestParam("userId") int requestedUserId) {
        UserDetails loggedUserDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        int loggedUserId = loggedUserDetails.getId();

        UserModel loggedUser = userService.findById(loggedUserId);
        UserModel requestedUser = userService.findById(requestedUserId);

        return chatService.createChat(loggedUser, requestedUser);
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