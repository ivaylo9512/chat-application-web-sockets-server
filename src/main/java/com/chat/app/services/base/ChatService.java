package com.chat.app.services.base;

import com.chat.app.models.Chat;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.specs.MessageSpec;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService {

    Chat findById(long id);

    Page<Chat> findUserChats(long id, int pageSize, LocalDateTime lastUpdatedAt, long lastId);

    Message addNewMessage(MessageSpec messageDto);

    Chat findUsersChat(long firstUser, long secondUser);

    List<Session> findSessions(long chatId, int page, int pageSize);
}
