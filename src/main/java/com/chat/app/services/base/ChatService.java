package com.chat.app.services.base;

import com.chat.app.models.DTOs.ChatDto;
import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;

import java.util.List;

public interface ChatService {
    List<ChatDto> getUserChats(int id, int pageSize);

    MessageDto addNewMessage(MessageDto messageDto);

    ChatDto createChat(UserModel loggedUserId, UserModel requestedUserId);

    boolean findIfUsersHaveChat(int firstUser, int secondUser);

    List<Session> getChatSessions(int chatId, int page, int pageSize);

}
