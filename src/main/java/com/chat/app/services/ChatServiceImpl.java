package com.chat.app.services;

import com.chat.app.models.Chat;
import com.chat.app.models.DTOs.ChatDto;
import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.compositePK.SessionPK;
import com.chat.app.repositories.base.ChatRepository;
import com.chat.app.repositories.base.MessageRepository;
import com.chat.app.repositories.base.SessionRepository;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private UserService userService;
    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;

    ChatServiceImpl(UserService userService, ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository){
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ChatDto> getUserChats(int id, int pageSize) {
        List<Chat> chats = chatRepository.findUserChats(id);
        chats.forEach(chat -> chat
                .setSessions(sessionRepository
                        .getSessions(chat,
                                PageRequest.of(0, pageSize, Sort.Direction.DESC, "session_date"))));
        return chats.stream()
                .map(ChatDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Session> getChatSessions(int chatId, int page, int pageSize){
        return sessionRepository.getSessions(chatRepository.getOne(chatId), PageRequest.of(page, pageSize, Sort.Direction.DESC, "session_date"));
    }

    @Override
    public MessageDto addNewMessage(MessageDto messageDto) {
        int sender = messageDto.getSenderId();
        int receiver = messageDto.getReceiverId();

        Chat chat = chatRepository.findById(messageDto.getChatId());

        int chatFirstUser = chat.getFirstUserModel().getId();
        int chatSecondUser = chat.getSecondUserModel().getId();

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));
        Message message = new Message(messageDto.getReceiverId(),LocalTime.now(),messageDto.getMessage(),session);
        message = messageRepository.save(message);

        messageDto.setTime(message.getTime());
        messageDto.setSession(session.getDate());

        return messageDto;
    }

    @Override
    public ChatDto createChat(int loggedUserId, int requestedUserId) {

        UserModel loggedUser = userService.findById(loggedUserId);
        UserModel requestedUser = userService.findById(requestedUserId);

        Chat chat = new Chat(loggedUser, requestedUser);

        return new ChatDto(chatRepository.save(chat));
    }
}