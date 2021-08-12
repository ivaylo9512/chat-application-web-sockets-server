package unit;

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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceImplTest {

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

    @Test(expected = EntityNotFoundException.class)
    public void findById_withNonExistingChat_shouldThrow() {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        chatService.findById(1L);
    }

    @Test()
    public void findById() {
        Chat chat = new Chat();

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        Chat foundChat = chatService.findById(1L);

        Assert.assertEquals(chat, foundChat);
    }

    @Test(expected = EntityNotFoundException.class)
    public void addNewMessage_withChatThatIsNotWithSameSenderAndReceiverId_shouldThrow() {
        UserModel sender = new UserModel();
        sender.setId(1);
        UserModel receiver = new UserModel();
        receiver.setId(5);

        Chat chat = new Chat();
        chat.setFirstUserModel(sender);
        chat.setSecondUserModel(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        chatService.addNewMessage(messageSpec);
    }

    @Test()
    public void addNewMessage_withNewSession() {
        UserModel sender = new UserModel();
        sender.setId(2);
        UserModel receiver = new UserModel();
        receiver.setId(3);

        Session session = new Session();

        Chat chat = new Chat();
        chat.setFirstUserModel(sender);
        chat.setSecondUserModel(receiver);

        MessageSpec messageSpec = new MessageSpec(1, 2, 3, "message");
        Message message = new Message(sender, LocalTime.now(), messageSpec.getMessage(), session);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(sessionRepository.findById(new SessionPK(chat, LocalDate.now()))).thenReturn(Optional.of(session));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.getOne(3L)).thenReturn(new UserModel());

        Message savedMessage = chatService.addNewMessage(messageSpec);

        Assert.assertEquals(message, savedMessage);
    }

    @Test()
    public void findUsersChats() {
        List<Session> sessions = new ArrayList<>(Arrays.asList(new Session(), new Session()));
        Chat chat = new Chat();
        chat.setId(1);

        when(chatRepository.findUsersChat(1, 2)).thenReturn(chat);
        when(sessionRepository.findSessions(chatRepository.getOne(1L),
                PageRequest.of(0, 5, Sort.Direction.DESC, "session_date"))).thenReturn(sessions);

        Chat foundChat = chatService.findUsersChat(1, 2);

        Assert.assertEquals(sessions, foundChat.getSessions());
    }


    @Test()
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

        when(chatRepository.findNextUserChats(1, 0, "2021-02-02", PageRequest.of(0, 5)))
                .thenReturn(page);
        when(sessionRepository.findSessions(chat, PageRequest.of(0, 5,
                Sort.Direction.DESC, "session_date"))).thenReturn(sessions);

        Page<Chat> chatPage = chatService.findUserChats(1, 5, "2021-02-02", 0);


        Assert.assertEquals(chatPage.getTotalElements(), chats.size());
        Assert.assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        Assert.assertEquals(chatPage.getContent(), chats);
        Assert.assertEquals(chat.getFirstUserModel(), user);
        Assert.assertEquals(chat.getSecondUserModel(), user2);
        Assert.assertEquals(chat1.getFirstUserModel(), user);
        Assert.assertEquals(chat1.getSecondUserModel(), user3);
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

        when(chatRepository.findNextUserChatsByName(1, "name", "lastName", 0, PageRequest.of(0, 5)))
                .thenReturn(page);
        when(sessionRepository.findSessions(chat, PageRequest.of(0, 5,
                Sort.Direction.DESC, "session_date"))).thenReturn(sessions);

        Page<Chat> chatPage = chatService.findUserChatsByName(1, 5, "name", "lastName", 0);

        Assert.assertEquals(chatPage.getTotalElements(), chats.size());
        Assert.assertEquals(chatPage.getContent().get(0).getSessions(), sessions);
        Assert.assertEquals(chatPage.getContent(), chats);
        Assert.assertEquals(chat.getFirstUserModel(), user);
        Assert.assertEquals(chat.getSecondUserModel(), user2);
        Assert.assertEquals(chat1.getFirstUserModel(), user);
        Assert.assertEquals(chat1.getSecondUserModel(), user3);
    }
}
