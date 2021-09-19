package com.chat.app.controllers;

import com.chat.app.models.*;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.MessageDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.SessionDto;
import com.chat.app.models.specs.MessageSpec;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.chat.app.security.Jwt;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import com.chat.app.models.UserDetails;

@RestController
@RequestMapping("/api/chats/auth")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping(value = {"/findChats/{pageSize}", "/findChats/{pageSize}/{lastUpdateAt}/{lastId}"})
    public PageDto<ChatDto> findChats(
            @PathVariable(name = "pageSize") int pageSize,
            @PathVariable(name = "lastUpdateAt", required = false) String lastUpdatedAt,
            @PathVariable(name = "lastId", required = false) Long lastId
    ){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        long userId = loggedUser.getId();

        Page<Chat> page = chatService.findUserChats(userId, pageSize, lastUpdatedAt, lastId == null ? 0 : lastId);

        List<ChatDto> chatDtos = page.getContent().stream().map(ChatDto::new).collect(Collectors.toList());

        return new PageDto<>(page.getTotalPages(), chatDtos);
    }

    @GetMapping(value = {"/findChatsByName/{pageSize}/{name}", "/findChatsByName/{pageSize}",
            "/findChatsByName/{pageSize}/{lastName}/{lastId}", "/findChatsByName/{pageSize}/{name}/{lastName}/{lastId}"})
    public PageDto<ChatDto> findChatsByName(
            @PathVariable(name = "pageSize") int pageSize,
            @PathVariable(name = "name", required = false) String name,
            @PathVariable(name = "lastName", required = false) String lastName,
            @PathVariable(name = "lastId", required = false) Long lastId
    ){
        UserDetails loggedUser = (UserDetails)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
        long userId = loggedUser.getId();

        Page<Chat> page = chatService.findUserChatsByName(userId, pageSize, name == null ?
                "" : name, lastName, lastId == null ? 0 : lastId);

        List<ChatDto> chatDtos = page.getContent().stream().map(ChatDto::new).collect(Collectors.toList());

        return new PageDto<>(page.getTotalElements(), chatDtos);
    }

    @GetMapping(value = "/findNextSessions/{chatId}/{lastSession}")
    public List<SessionDto> findNextSessions(
            @PathVariable(name = "chatId") long chatId,
            @PathVariable(name = "lastSession") String lastSession){
        return chatService.findSessions(chatId, lastSession).stream().map(SessionDto::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping(value = "/delete/{id}")
    public void delete(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        chatService.delete(id, userService.findById(loggedUser.getId()));
    }

    @MessageMapping("/message")
    public void message(@Valid MessageSpec messageSpec, SimpMessageHeaderAccessor headers) throws  Exception {
        UserDetails loggedUser;
        try{
            String auth = headers.getNativeHeader("Authorization").get(0);
            String token = auth.substring(6);
            loggedUser = Jwt.validate(token);
        }catch (Exception e){
            throw new BadCredentialsException("Jwt token is missing or is incorrect.");
        }
        messageSpec.setSenderId(loggedUser.getId());
        MessageDto message = new MessageDto(chatService.addNewMessage(messageSpec));

        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()), "/message", message);
    }

    @GetMapping("/findByUser/{id}")
    public ChatDto findChatByUser(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Chat chat = chatService.findUsersChat(loggedUser.getId(), id);

        if(chat == null){
            throw new EntityNotFoundException("Chat not found.");
        }

        return new ChatDto(chat);
    }
}