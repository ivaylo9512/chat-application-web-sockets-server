package com.chat.app.services.base;

import com.chat.app.models.Chat;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.MessageSpec;

import java.util.List;
import java.util.Map;

public interface ChatService {

    Chat findById(long id);

    List<Chat> findUserChats(long id, int pageSize);

    Message addNewMessage(MessageSpec messageDto);

    Chat createChat(UserModel loggedUser, UserModel requestedUser);

    Chat findUsersChat(long firstUser, long secondUser);

    List<Session> findSessions(long chatId, int page, int pageSize);
}
