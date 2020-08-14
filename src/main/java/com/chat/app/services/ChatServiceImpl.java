package com.chat.app.services;

import com.chat.app.exceptions.ChatNotFoundException;
import com.chat.app.models.Chat;
import com.chat.app.models.DTOs.ChatDto;
import com.chat.app.models.DTOs.MessageDto;
import com.chat.app.models.DTOs.UserDto;
import com.chat.app.models.Message;
import com.chat.app.models.Session;
import com.chat.app.models.UserModel;
import com.chat.app.models.compositePK.SessionPK;
import com.chat.app.repositories.base.ChatRepository;
import com.chat.app.repositories.base.MessageRepository;
import com.chat.app.repositories.base.SessionRepository;
import com.chat.app.services.base.ChatService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepository;
    private SessionRepository sessionRepository;
    private MessageRepository messageRepository;

    ChatServiceImpl(ChatRepository chatRepository, SessionRepository sessionRepository, MessageRepository messageRepository){
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Chat findById(int id) {
        return chatRepository.findById(id)
                .orElseThrow(()-> new ChatNotFoundException(String.format("Chat with id %d is not found.", id)));
    }

    @Override
    public List<ChatDto> findUserChats(int id, int pageSize) {
        List<Chat> chats = chatRepository.findUserChats(id, PageRequest.of(0, pageSize));
        chats.forEach(chat -> chat
                .setSessions(sessionRepository
                        .findSessions(chat,
                                PageRequest.of(0, pageSize, Sort.Direction.DESC, "session_date"))));
        List<ChatDto> chatDtos = new ArrayList<>();
        chats.forEach(chat -> {
            ChatDto chatDto = new ChatDto(chat);
            if(chat.getFirstUserModel().getId() == id){
                chatDto.setUser(new UserDto(chat.getSecondUserModel()));
            }else{
                chatDto.setUser(new UserDto(chat.getFirstUserModel()));
            }
            chatDtos.add(chatDto);
        });
        return chatDtos;
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
    @Transactional
    public MessageDto addNewMessage(MessageDto messageDto) {
        Chat chat = findById(messageDto.getChatId());
        verifyMessage(messageDto, chat);

        Session session = sessionRepository.findById(new SessionPK(chat,LocalDate.now()))
                .orElse(new Session(chat, LocalDate.now()));
        Message message = new Message(messageDto.getReceiverId(),LocalTime.now(),messageDto.getMessage(),session);
        message = messageRepository.save(message);

        String username = chat.getFirstUserModel().getId() == messageDto.getReceiverId()
                ? chat.getFirstUserModel().getUsername() : chat.getSecondUserModel().getUsername();

        messageDto.setTime(message.getTime());
        messageDto.setSession(session.getDate());
        messageDto.setUsername(username);

        return messageDto;
    }

    private void verifyMessage(MessageDto messageDto, Chat chat) {
        int sender = messageDto.getSenderId();
        int receiver = messageDto.getReceiverId();

        int chatFirstUser = chat.getFirstUserModel().getId();
        int chatSecondUser = chat.getSecondUserModel().getId();

        if ((sender != chatFirstUser && sender != chatSecondUser) || (receiver != chatFirstUser && receiver != chatSecondUser)) {
            throw new ChatNotFoundException("Users don't match the given chat.");
        }
    }

    @Override
    public ChatDto createChat(UserModel loggedUser, UserModel requestedUser) {

        if(findIfUsersHaveChat(loggedUser.getId(), requestedUser.getId())){
            throw new RuntimeException("Chat already exist");
        }

        Chat chat = new Chat(loggedUser, requestedUser);
        ChatDto chatDto = new ChatDto(chatRepository.save(chat));
        chatDto.setUser(new UserDto(requestedUser));

        return chatDto;
    }
}