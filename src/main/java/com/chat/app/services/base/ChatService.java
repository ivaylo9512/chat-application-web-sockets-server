package com.chat.app.services.base;

import com.chat.app.models.DTOs.ChatDto;

import java.util.List;

public interface ChatService {
    List<ChatDto> findUserChats(int id, int pageSize);

    ChatDto createChat(int loggedUserId, int requestedUserId);
}
