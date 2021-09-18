package com.chat.app.controllers;

import com.chat.app.models.Chat;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.RequestDto;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.Request;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.RequestService;
import com.chat.app.services.base.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/requests")
public class RequestController {
    private final UserService userService;
    private final ChatService chatService;
    private final RequestService requestService;

    public RequestController(UserService userService, ChatService chatService, RequestService requestService) {
        this.userService = userService;
        this.chatService = chatService;
        this.requestService = requestService;
    }

    @PostMapping("/auth/add/{id}")
    public UserDto addRequest(@PathVariable("id") long id) {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        UserModel user = userService.findById(loggedUser.getId());
        UserModel receiver = userService.findById(id);

        Chat chat = chatService.findUsersChat(loggedUser.getId(), receiver.getId());
        if (chat != null) {
            return new UserDto(receiver, chat);
        }

        Request request = requestService.findByUsers(loggedUser.getId(), id);
        if(request != null){
            if(request.getReceiver().getId() == loggedUser.getId()){
                requestService.delete(request);
                return new UserDto(receiver, chatService.create(user, receiver));
            }
            return new UserDto(receiver, request);
        }

        return new UserDto(receiver, requestService.create(user, receiver));
    }

    @GetMapping("/auth/findById/{id}")
    public RequestDto findById(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new RequestDto(requestService.findById(id, loggedUser.getId()));
    }

    @GetMapping("/auth/findByUser/{id}")
    public RequestDto findByUser(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new RequestDto(requestService.findRequest(id, loggedUser.getId()));
    }

    @GetMapping(value = {"/auth/findAll/{pageSize}", "/auth/findAll/{pageSize}/{lastCreatedAt}/{lastId}"})
    public PageDto<RequestDto> findAll(
            @PathVariable("pageSize") int pageSize,
            @PathVariable(value = "lastCreatedAt", required = false) String lastCreatedAt,
            @PathVariable(value = "lastId", required = false) Long lastId){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        Page<Request> page = requestService.findAll(loggedUser.getId(), pageSize, lastCreatedAt, lastId != null ? lastId : 0);
        return new PageDto<>(page.getTotalElements(),
                page.getContent().stream()
                        .map(RequestDto::new)
                        .collect(Collectors.toList()));
    }

    @PostMapping("/auth/accept/{id}")
    public ChatDto acceptRequest(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        Request request = requestService.verifyAccept(id, loggedUser);
        Chat chat = chatService.create(request.getReceiver(), request.getSender());
        requestService.delete(request);

        return new ChatDto(chat);
    }

    @PostMapping("/auth/deny/{id}")
    public boolean denyRequest(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        Request request = requestService.verifyDeny(id, loggedUser);
        requestService.delete(request);

        return true;
    }
}
