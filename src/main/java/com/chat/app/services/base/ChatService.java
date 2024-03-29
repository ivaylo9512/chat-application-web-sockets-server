package com.chat.app.services.base;

import com.chat.app.models.Chat;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.MessageSpec;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ChatService {

    Chat findById(long id, long loggedUser);

    Page<Chat> findUserChats(long id, int pageSize, String lastUpdatedAt, long lastId);

    Page<Chat> findUserChatsByName(long id, int pageSize, String name, String lastName, long lastId);

    Message addNewMessage(MessageSpec messageDto);

    Chat findUsersChat(long firstUser, long secondUser);

    List<Session> findSessions(long chatId, String lastSessions);

    Chat create(UserModel firstUser, UserModel secondUser);

    void delete(long id, UserModel user);
}
