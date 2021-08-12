package com.tick42.quicksilver.services;

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
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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

}
