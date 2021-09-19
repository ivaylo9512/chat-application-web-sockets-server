package unit;

import com.chat.app.controllers.ChatController;
import com.chat.app.models.Chat;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.SessionDto;
import com.chat.app.models.Session;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.services.base.ChatService;
import com.chat.app.services.base.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {
    @InjectMocks
    public ChatController chatController;

    @Mock
    public ChatService chatService;

    @Mock
    public UserService userService;

    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria");
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());
    private final LocalDateTime dateTime = LocalDateTime.of(2021, 9, 9, 9, 10);

    @Test
    public void findUserChats(){
        UserModel secondUser = new UserModel();
        secondUser.setId(2);

        Chat chat = new Chat(userModel, secondUser);
        chat.setId(1);
        chat.setUpdatedAt(dateTime);
        chat.setCreatedAt(dateTime);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(chatService.findUsersChat(1L, 2L)).thenReturn(chat);

        ChatDto chatDto = chatController.findChatByUser(2L);

        assertEquals(chatDto.getId(), chat.getId());
        assertEquals(chatDto.getFirstUser().getId(), user.getId());
        assertEquals(chatDto.getSecondUser().getId(), secondUser.getId());
        assertEquals(chatDto.getCreatedAt(), dateTime.toString());
        assertEquals(chatDto.getUpdatedAt(), dateTime.toString());
    }

    @Test
    public void findUserChats_WithChatNotFound() {
        UserModel secondUser = new UserModel();
        secondUser.setId(2);

        Chat chat = new Chat(userModel, secondUser);
        chat.setId(1);
        chat.setUpdatedAt(dateTime);
        chat.setCreatedAt(dateTime);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(chatService.findUsersChat(1L, 2L)).thenReturn(null);

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> chatController.findChatByUser(2L));

        assertEquals(thrown.getMessage(), "Chat not found.");
    }

    @Test
    public void delete(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(1L)).thenReturn(userModel);

        chatController.delete(1L);

        verify(chatService, times(1)).delete(1L, userModel);
    }

    @Test
    public void findNextSessions(){
        Chat chat = new Chat();
        chat.setId(1);

        Session session = new Session(chat, LocalDate.of(2021, 10, 10));
        Session session1 = new Session(chat, LocalDate.of(2021, 10, 11));
        Session session2 = new Session(chat, LocalDate.of(2021, 10, 12));

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(chatService.findSessions(1L, "2021-10-09")).thenReturn(List.of(session, session1, session2));

        List<SessionDto> sessions = chatController.findNextSessions(1L, "2021-10-09");

        assertEquals(sessions.get(0).getDate().toString(), "2021-10-10");
        assertEquals(sessions.get(1).getDate().toString(), "2021-10-11");
        assertEquals(sessions.get(2).getDate().toString(), "2021-10-12");
    }

    @Test
    public void findChats(){
        UserModel secondUser = new UserModel();
        secondUser.setId(2);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Chat chat = new Chat(userModel, secondUser);
        chat.setUpdatedAt(dateTime);
        chat.setCreatedAt(dateTime);

        Session session = new Session(chat, LocalDate.of(2021, 10, 10));
        Session session1 = new Session(chat, LocalDate.of(2021, 10, 11));
        Session session2 = new Session(chat, LocalDate.of(2021, 10, 12));

        chat.setId(1);
        chat.setSessions(List.of(session, session1, session2));

        Chat chat1 = new Chat(userModel, secondUser);
        chat1.setId(2);
        chat1.setUpdatedAt(dateTime);
        chat1.setCreatedAt(dateTime);

        Page<Chat> page = new PageImpl<>(List.of(chat, chat1));

        when(chatService.findUserChats(1L, 3, "2021-10-10 20:05", 2L)).thenReturn(page);

        PageDto<ChatDto> pageDto = chatController.findChats(3, "2021-10-10 20:05", 2L);
        List<SessionDto> sessions = pageDto.getData().get(0).getSessions();

        assertEquals(pageDto.getCount(), 1);
        assertEquals(pageDto.getData().size(), 2);
        assertEquals(sessions.get(0).getDate().toString(), "2021-10-10");
        assertEquals(sessions.get(1).getDate().toString(), "2021-10-11");
        assertEquals(sessions.get(2).getDate().toString(), "2021-10-12");
    }

    @Test
    public void findChatsByName() {
        UserModel secondUser = new UserModel();
        secondUser.setId(2);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Chat chat = new Chat(userModel, secondUser);
        chat.setUpdatedAt(dateTime);
        chat.setCreatedAt(dateTime);

        Session session = new Session(chat, LocalDate.of(2021, 10, 10));
        Session session1 = new Session(chat, LocalDate.of(2021, 10, 11));
        Session session2 = new Session(chat, LocalDate.of(2021, 10, 12));

        chat.setId(1);
        chat.setSessions(List.of(session, session1, session2));

        Chat chat1 = new Chat(userModel, secondUser);
        chat1.setId(2);
        chat1.setUpdatedAt(dateTime);
        chat1.setCreatedAt(dateTime);

        Page<Chat> page = new PageImpl<>(List.of(chat, chat1));

        when(chatService.findUserChatsByName(1L, 3, "name", "lastName", 3L)).thenReturn(page);

        PageDto<ChatDto> pageDto = chatController.findChatsByName(3, "name", "lastName", 3L);
        List<SessionDto> sessions = pageDto.getData().get(0).getSessions();

        assertEquals(pageDto.getCount(), 2);
        assertEquals(pageDto.getData().size(), 2);
        assertEquals(sessions.get(0).getDate().toString(), "2021-10-10");
        assertEquals(sessions.get(1).getDate().toString(), "2021-10-11");
        assertEquals(sessions.get(2).getDate().toString(), "2021-10-12");
    }
}
