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

import java.util.List;


@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/auth/chat/getChats")
    public List<ChatDto> getChats(@RequestParam(name = "pageSize") int pageSize){
        UserDetails userDetails = (UserDetails)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        int userId = userDetails.getId();

        return chatService.getUserChats(userId, pageSize);

    }

    @GetMapping(value = "/chat/auth/nextSessions")
    public List<Session> getChatSessions(
            @RequestParam(name = "chatId") int chatId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "pageSize") int pageSize){
        return chatService.getChatSessions(chatId, page, pageSize);
    }

    @PostMapping("auth/create")
    public ChatDto createChat(@RequestParam("userId") int requestedUserId){
        UserDetails loggedUserDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        int loggedUserId = loggedUserDetails.getId();

        UserModel loggedUser = userService.findById(loggedUserId);
        UserModel requestedUser = userService.findById(requestedUserId);

        return chatService.createChat(loggedUser, requestedUser);
    }
    @PostMapping(value = "/newMessage")
    public MessageDto newMessage(@RequestBody MessageDto message){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        message.setSenderId(userDetails.getId());
        return chatService.addNewMessage(message);
    }

}