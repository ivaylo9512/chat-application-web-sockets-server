package com.chat.app.services;

import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.*;
import com.chat.app.models.compositePK.SessionPK;
import com.chat.app.models.specs.MessageSpec;
import com.chat.app.repositories.base.ChatRepository;
import com.chat.app.repositories.base.MessageRepository;
import com.chat.app.repositories.base.SessionRepository;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.base.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository, UserRepository userRepository){
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Chat findById(long id, long loggedUser) {
        Chat chat = chatRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Chat not found."));

        if(chat.getFirstUserModel().getId() != loggedUser &&
                chat.getSecondUserModel().getId() != loggedUser){
            throw new UnauthorizedException("Unauthorized.");
        }
        return chat;
    }

    @Override
    public Page<Chat> findUserChats(long userId, int pageSize, String lastUpdatedAt, long lastId) {
        Page<Chat> chatsPage;

        if(lastUpdatedAt == null){
            chatsPage = chatRepository.findUserChats(userId, PageRequest.of(0, pageSize));
        }else{
            chatsPage = chatRepository.findNextUserChats(userId, lastId, lastUpdatedAt, PageRequest.of(0, pageSize));
        }

        chatsPage.getContent().forEach(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUserModel();
            if (loggedUser.getId() != userId) {
                chat.setFirstUserModel(chat.getSecondUserModel());
                chat.setSecondUserModel(loggedUser);
            }
        });

        return chatsPage;
    }

    @Override
    public Page<Chat> findUserChatsByName(long userId, int pageSize, String name, String lastName, long lastId) {
        Page<Chat> chatsPage;

        if(lastName == null){
            chatsPage = chatRepository.findUserChatsByName(userId, name, PageRequest.of(0, pageSize));
        }else{
            chatsPage = chatRepository.findNextUserChatsByName(userId, name, lastName, lastId, PageRequest.of(0, pageSize));
        }

        chatsPage.getContent().forEach(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUserModel();
            if (loggedUser.getId() != userId) {
                chat.setFirstUserModel(chat.getSecondUserModel());
                chat.setSecondUserModel(loggedUser);
            }
        });

        return chatsPage;
    }

    @Override
    public Chat findUsersChat(long firstUser, long secondUser){
        Chat chat = chatRepository.findUsersChat(firstUser, secondUser);
        if(chat != null){
            chat.setSessions(findSessions(chat.getId(), 0, 5));
        }
        return chat;
    }

    @Override
    public List<Session> findSessions(long chatId, int page, int pageSize){
        return sessionRepository.findSessions(chatRepository.getOne(chatId),
                PageRequest.of(page, pageSize, Sort.Direction.DESC, "session_date"));
    }

    @Override
    public Message addNewMessage(MessageSpec messageSpec) {
        Chat chat = chatRepository.findById(messageSpec.getChatId())
                .orElseThrow(()-> new EntityNotFoundException("Chat with id: " + messageSpec.getChatId() + " is not found."));

        verifyMessage(messageSpec, chat);

        Session session = sessionRepository.findById(new SessionPK(chat, LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));

        UserModel user = userRepository.getOne(messageSpec.getReceiverId());
        Message message = new Message(user, LocalTime.now(), messageSpec.getMessage(), session);

        return messageRepository.save(message);
    }

    private void verifyMessage(MessageSpec message, Chat chat) {
        long sender = message.getSenderId();
        long receiver = message.getReceiverId();

        long chatFirstUser = chat.getFirstUserModel().getId();
        long chatSecondUser = chat.getSecondUserModel().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new UnauthorizedException("Users don't match the given chat.");
        }
    }

    @Override
    public Chat create(UserModel firstUser, UserModel secondUser){
        return chatRepository.save(new Chat(firstUser, secondUser));
    }
}