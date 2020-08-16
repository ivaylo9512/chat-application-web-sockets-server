package com.chat.app.services;

import com.chat.app.models.Chat;
import com.chat.app.models.DTOs.MessageDto;
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
    public Chat findById(int id) {
        return chatRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Chat with id %d is not found.", id)));
    }

    @Override
    public Map<Integer, Chat> findUserChats(int id, int pageSize) {
        Map<Integer, Chat> chatsMap = new LinkedHashMap<>();
        chatRepository.findUserChats(id, PageRequest.of(0, pageSize)).forEach(chat -> {
            chat.setSessions(sessionRepository.findSessions(chat, PageRequest.of(0, pageSize,
                    Sort.Direction.DESC, "session_date")));

            UserModel loggedUser = chat.getFirstUserModel();
            if(loggedUser.getId() != id){
                chat.setFirstUserModel(chat.getSecondUserModel());
                chat.setSecondUserModel(loggedUser);
            }

            chatsMap.put(chat.getId(), chat);
        });
        return chatsMap;
    }

    @Override
    public boolean findIfUsersHaveChat(int firstUser, int secondUser){
        return chatRepository.findIfUsersHaveChat(firstUser, secondUser) != null;
    }

    @Override
    public List<Session> findSessions(int chatId, int page, int pageSize){
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
        int sender = message.getSenderId();
        int receiver = message.getReceiverId();

        int chatFirstUser = chat.getFirstUserModel().getId();
        int chatSecondUser = chat.getSecondUserModel().getId();

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