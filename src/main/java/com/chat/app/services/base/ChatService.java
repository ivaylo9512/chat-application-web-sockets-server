package com.chat.app.services.base;

import com.chat.app.models.Chat;
import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;

import java.util.List;

public interface ChatService {

    Chat findById(int id);

    List<Chat> findUserChats(int id, int pageSize);

    MessageDto addNewMessage(MessageDto messageDto);

    Chat createChat(UserModel loggedUser, UserModel requestedUser);

    boolean findIfUsersHaveChat(int firstUser, int secondUser);

    List<Session> findSessions(int chatId, int page, int pageSize);
}
