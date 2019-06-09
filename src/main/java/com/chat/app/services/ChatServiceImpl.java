package com.chat.app.services;

import com.chat.app.models.Chat;
import com.chat.app.models.DTOs.ChatDto;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.ChatRepository;
import com.chat.app.repositories.base.SessionRepository;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private UserService userService;
    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;

    ChatServiceImpl(UserService userService, ChatRepository chatRepository, SessionRepository sessionRepository){
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
    }


    @Override
    public ChatDto createChat(int loggedUserId , int requestedUserId) {

        UserModel loggedUser = userService.findById(loggedUserId);
        UserModel requestedUser = userService.findById(requestedUserId);

        Chat chat = new Chat(loggedUser, requestedUser);

        return new ChatDto(chatRepository.save(chat));
    }
}