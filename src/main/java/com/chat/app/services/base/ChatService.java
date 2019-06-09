package com.chat.app.services.base;

import com.chat.app.models.DTOs.ChatDto;

public interface ChatService {
    ChatDto createChat(int loggedUserId, int requestedUserId);
}
