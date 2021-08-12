package com.chat.app.controllers;

import com.chat.app.models.Chat;
import com.chat.app.models.Dtos.PageDto;
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

    @PostMapping("/auth/addRequest/{id}")
    public UserDto addRequest(@PathVariable("id") long id) {
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        UserModel user = userService.findById(id);
        Chat chat = chatService.findUsersChat(loggedUser.getId(), user.getId());
        if (chat != null) {
            return new UserDto(user, chat);
        }

        Request request = requestService.findByUsers(loggedUser.getId(), id);
        if(request != null){
            if(request.getTo().getId() == loggedUser.getId()){
                requestService.delete(request.getId());
                return new UserDto(user, chatService.create(loggedUser.getId(), id));
            }
            return new UserDto(user);
        }

        requestService.create(loggedUser.getId(), id);

        return new UserDto(user);
    }

    @GetMapping(value = {"/auth/findAll{pageSize}", "/auth/findAll{pageSize}/{lastCreatedAt}/{lastId}"})
    private PageDto<Request> findAll(
            @PathVariable("pageSize") int pageSize,
            @PathVariable(value = "lastCreatedAt", required = false) String lastCreatedAt,
            @PathVariable(value = "lastId", required = false) long lastId){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        Page<Request> page = requestService.findAll(loggedUser.getId(), pageSize, lastCreatedAt, lastId);
        return new PageDto<>(page.getTotalElements(), page.getContent());
    }
}
