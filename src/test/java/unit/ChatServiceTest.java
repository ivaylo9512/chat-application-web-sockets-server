package unit;

import com.chat.app.exceptions.UnauthorizedException;
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
import com.chat.app.services.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    public void findById_withNonExistingChat_shouldThrow() {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> chatService.findById(1L, 1L)
        );

        assertEquals(thrown.getMessage(), "Chat not found.");
    }

    @Test
    public void findById() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);
        Chat chat = new Chat(firsUser, secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        Chat foundChat = chatService.findById(1L, 2L);

        assertEquals(chat, foundChat);
    }

    @Test
    public void findById_WithSecondUser() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);
        Chat chat = new Chat(firsUser, secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        Chat foundChat = chatService.findById(1L, 1L);

        assertEquals(chat, foundChat);
    }

    @Test
    public void findById_WithChatThatDoesNotBelongToUser() {
        UserModel firsUser = new UserModel();
        UserModel secondUser = new UserModel();
        firsUser.setId(1);
        secondUser.setId(2);
        Chat chat = new Chat(firsUser, secondUser);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.findById(1L, 3L)
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentSender() {
        UserModel sender = new UserModel();
        sender.setId(1);
        UserModel receiver = new UserModel();
        receiver.setId(5);

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentSender_WithChatSecondUser() {
        UserModel sender = new UserModel();
        sender.setId(1);
        UserModel receiver = new UserModel();
        receiver.setId(5);

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentReceiver() {
        UserModel sender = new UserModel();
        sender.setId(5);
        UserModel receiver = new UserModel();
        receiver.setId(1);

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withChatWithDifferentReceiver_WithChatSecondUser() {
        UserModel sender = new UserModel();
        sender.setId(5);
        UserModel receiver = new UserModel();
        receiver.setId(1);

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 5, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> chatService.addNewMessage(messageSpec)
        );

        assertEquals(thrown.getMessage(), "Users don't match the given chat.");
    }

    @Test
    public void addNewMessage_withNewSession() {
        UserModel sender = new UserModel();
        sender.setId(2);
        UserModel receiver = new UserModel();
        receiver.setId(3);

        Session session = new Session();

        Chat chat = new Chat();
        chat.setFirstUser(sender);
        chat.setSecondUser(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");
        Message message = new Message(sender, LocalTime.now(), messageSpec.getMessage(), session);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(sessionRepository.findById(new SessionPK(chat, LocalDate.now()))).thenReturn(Optional.of(session));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.getById(3L)).thenReturn(new UserModel());

        Message savedMessage = chatService.addNewMessage(messageSpec);

        assertEquals(message, savedMessage);
    }

    @Test
    public void addNewMessage_WithReceiver() {
        UserModel sender = new UserModel();
        sender.setId(2);
        UserModel receiver = new UserModel();
        receiver.setId(3);

        Session session = new Session();

        Chat chat = new Chat();
        chat.setFirstUser(receiver);
        chat.setSecondUser(sender);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");
        Message message = new Message(sender, LocalTime.now(), messageSpec.getMessage(), session);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(sessionRepository.findById(new SessionPK(chat, LocalDate.now()))).thenReturn(Optional.of(session));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.getById(3L)).thenReturn(new UserModel());

        Message savedMessage = chatService.addNewMessage(messageSpec);

        assertEquals(message, savedMessage);
    }

    @Test
    public void findUsersChats() {
        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));
        Chat chat = new Chat();
        chat.setId(1);

        when(chatRepository.findUsersChat(1, 2)).thenReturn(chat);
        when(sessionRepository.findSessions(chatRepository.getById(1L),
                PageRequest.of(0, 3, Sort.Direction.DESC, "session_date"))).thenReturn(sessions);

        Chat foundChat = chatService.findUsersChat(1, 2);

        assertEquals(sessions, foundChat.getSessions());
    }

    @Test
    public void findUserChats(){
        UserModel user = new UserModel();
        UserModel user2 = new UserModel();
        UserModel user3 = new UserModel();
        UserModel user4 = new UserModel();
        UserModel user5 = new UserModel();
        user.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);

        Chat chat = new Chat(user, user2);
        Chat chat1 = new Chat(user3, user);
        Chat chat2 = new Chat(user, user4);
        Chat chat3 = new Chat(user5, user);

        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));

        List<Chat> chats = new ArrayList<>(Arrays.asList(chat, chat1, chat2, chat3));
        Page<Chat> page = new PageImpl<>(chats);

        when(chatRepository.findUserChats(1, PageRequest.of(0, 5)))
                .thenReturn(page);
        when(chatService.findSessions(chat.getId(), null)).thenReturn(sessions);

        Page<Chat> chatPage = chatService.findUserChats(1, 5, null, 0);

        assertEquals(chatPage.getTotalElements(), chats.size());
        assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        assertEquals(chatPage.getContent(), chats);
        assertEquals(chat.getFirstUser(), user);
        assertEquals(chat.getSecondUser(), user2);
        assertEquals(chat1.getFirstUser(), user);
        assertEquals(chat1.getSecondUser(), user3);
    }

    @Test
    public void findNextUserChats(){
        UserModel user = new UserModel();
        UserModel user2 = new UserModel();
        UserModel user3 = new UserModel();
        UserModel user4 = new UserModel();
        UserModel user5 = new UserModel();
        user.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);

        Chat chat = new Chat(user, user2);
        Chat chat1 = new Chat(user3, user);
        Chat chat2 = new Chat(user, user4);
        Chat chat3 = new Chat(user5, user);

        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));

        List<Chat> chats = new ArrayList<>(Arrays.asList(chat, chat1, chat2, chat3));
        Page<Chat> page = new PageImpl<>(chats);

        when(chatRepository.findNextUserChats(1, 0, "2021-02-02", PageRequest.of(0, 5)))
                .thenReturn(page);
        when(chatService.findSessions(chat.getId(), null)).thenReturn(sessions);


        Page<Chat> chatPage = chatService.findUserChats(1, 5, "2021-02-02", 0);


        assertEquals(chatPage.getTotalElements(), chats.size());
        assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        assertEquals(chatPage.getContent(), chats);
        assertEquals(chat.getFirstUser(), user);
        assertEquals(chat.getSecondUser(), user2);
        assertEquals(chat1.getFirstUser(), user);
        assertEquals(chat1.getSecondUser(), user3);
    }

    @Test()
    public void findUserChatsByName(){
        UserModel user = new UserModel();
        UserModel user2 = new UserModel();
        UserModel user3 = new UserModel();
        UserModel user4 = new UserModel();
        UserModel user5 = new UserModel();
        user.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);

        Chat chat = new Chat(user, user2);
        Chat chat1 = new Chat(user3, user);
        Chat chat2 = new Chat(user, user4);
        Chat chat3 = new Chat(user5, user);

        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));

        List<Chat> chats = new ArrayList<>(Arrays.asList(chat, chat1, chat2, chat3));
        Page<Chat> page = new PageImpl<>(chats);

        when(chatRepository.findUserChatsByName(1, "name", PageRequest.of(0, 5)))
                .thenReturn(page);
        when(chatService.findSessions(chat.getId(), null)).thenReturn(sessions);

        Page<Chat> chatPage = chatService.findUserChatsByName(1, 5, "name", null, 0);

        assertEquals(chatPage.getTotalElements(), chats.size());
        assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        assertEquals(chatPage.getContent(), chats);
        assertEquals(chat.getFirstUser(), user);
        assertEquals(chat.getSecondUser(), user2);
        assertEquals(chat1.getFirstUser(), user);
        assertEquals(chat1.getSecondUser(), user3);
    }

    @Test()
    public void findNextUserChatsByName(){
        UserModel user = new UserModel();
        UserModel user2 = new UserModel();
        UserModel user3 = new UserModel();
        UserModel user4 = new UserModel();
        UserModel user5 = new UserModel();
        user.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);
        user5.setId(5);

        Chat chat = new Chat(user, user2);
        Chat chat1 = new Chat(user3, user);
        Chat chat2 = new Chat(user, user4);
        Chat chat3 = new Chat(user5, user);

        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));

        List<Chat> chats = new ArrayList<>(Arrays.asList(chat, chat1, chat2, chat3));
        Page<Chat> page = new PageImpl<>(chats);

        when(chatRepository.findNextUserChatsByName(1, "name", "lastName", 0, PageRequest.of(0, 5)))
                .thenReturn(page);
        when(chatService.findSessions(chat.getId(), null)).thenReturn(sessions);

        Page<Chat> chatPage = chatService.findUserChatsByName(1, 5, "name", "lastName", 0);

        assertEquals(chatPage.getTotalElements(), chats.size());
        assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        assertEquals(chatPage.getContent(), chats);
        assertEquals(chat.getFirstUser(), user);
        assertEquals(chat.getSecondUser(), user2);
        assertEquals(chat1.getFirstUser(), user);
        assertEquals(chat1.getSecondUser(), user3);
    }

    @Test
    public void delete(){
        UserModel user = new UserModel();
        user.setId(1);
        user.setRole("ROLE_USER");

        Chat chat = new Chat();
        chat.setFirstUser(user);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        chatService.delete(1L, user);

        verify(chatRepository, times(1)).delete(chat);
    }

    @Test
    public void delete_WithAdmin(){
        UserModel user = new UserModel();
        user.setId(1);
        user.setRole("ROLE_ADMIN");

        chatService.delete(1L, user);

        verify(chatRepository, times(1)).deleteById(1L);
    }
}
