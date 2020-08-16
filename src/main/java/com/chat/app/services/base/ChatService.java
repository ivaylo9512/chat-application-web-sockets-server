package com.chat.app.services.base;

import com.chat.app.models.Chat;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.MessageSpec;

import java.util.List;
import java.util.Map;

public interface ChatService {

    Chat findById(int id);

    Map<Integer, Chat> findUserChats(int id, int pageSize);

    Message addNewMessage(MessageSpec messageDto);

    Chat createChat(UserModel loggedUser, UserModel requestedUser);

    boolean findIfUsersHaveChat(int firstUser, int secondUser);

    List<Session> findSessions(int chatId, int page, int pageSize);
}
