package com.chat.app.services;

import com.chat.app.models.Chat;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.compositePK.SessionPK;
import com.chat.app.models.specs.MessageSpec;
import com.chat.app.repositories.base.ChatRepository;
import com.chat.app.repositories.base.MessageRepository;
import com.chat.app.repositories.base.SessionRepository;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.base.ChatService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository, UserRepository userRepository){
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Chat findById(long id) {
        return chatRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Chat with id %d is not found.", id)));
    }

    @Override
    public List<Chat> findUserChats(long id, int pageSize) {
        return chatRepository.findUserChats(id, PageRequest.of(0, pageSize)).stream().map(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUserModel();
            if (loggedUser.getId() != id) {
                chat.setFirstUserModel(chat.getSecondUserModel());
                chat.setSecondUserModel(loggedUser);
            }
            return chat;
        }).collect(Collectors.toList());
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

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));

        UserModel user = userRepository.getOne(messageSpec.getReceiverId());
        Message message = new Message(user,LocalTime.now(),messageSpec.getMessage(),session);

        return messageRepository.save(message);
    }

    private void verifyMessage(MessageSpec message, Chat chat) {
        long sender = message.getSenderId();
        long receiver = message.getReceiverId();

        long chatFirstUser = chat.getFirstUserModel().getId();
        long chatSecondUser = chat.getSecondUserModel().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new EntityNotFoundException("Users don't match the given chat.");
        }
    }

    @Override
    public Chat createChat(UserModel loggedUser, UserModel requestedUser) {
        if(findIfUsersHaveChat(loggedUser.getId(), requestedUser.getId())){
            throw new RuntimeException("Chat already exist");
        }

        return chatRepository.save(new Chat(loggedUser, requestedUser));
    }
}