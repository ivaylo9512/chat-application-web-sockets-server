package unit;

import com.chat.app.controllers.RequestController;
import com.chat.app.models.*;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.RequestDto;
import com.chat.app.services.ChatServiceImpl;
import com.chat.app.services.RequestServiceImpl;
import com.chat.app.services.UserServiceImpl;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestsControllerTest {
    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestServiceImpl requestService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ChatServiceImpl chatService;

    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria");
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());
    private final LocalDateTime dateTime = LocalDateTime.of(2021, 9, 9, 9, 10);

    @Test
    public void findById(){
        UserModel sender = new UserModel();
        sender.setId(2);

        Request request = new Request(sender, userModel);
        request.setId(1);
        request.setCreatedAt(dateTime);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.findById(1L, user.getId())).thenReturn(request);

        RequestDto requestDto = requestController.findById(1L);

        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getCreatedAt(), dateTime.toString());
        assertEquals(requestDto.getReceiver().getId(), request.getReceiver().getId());
        assertEquals(requestDto.getSender().getId(), request.getSender().getId());
    }
    @GetMapping("/auth/findById/{id}")
    public RequestDto findById(@PathVariable("id") long id){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new RequestDto(requestService.findById(id, loggedUser.getId()));
    }

    @Test
    public void findByUser(){
        UserModel sender = new UserModel();
        sender.setId(2);

        Request request = new Request(sender, userModel);
        request.setId(1);
        request.setCreatedAt(dateTime);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.findRequest(2L, user.getId())).thenReturn(request);

        RequestDto requestDto  = requestController.findByUser(2L);

        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getCreatedAt(), dateTime.toString());
        assertEquals(requestDto.getReceiver().getId(), request.getReceiver().getId());
        assertEquals(requestDto.getSender().getId(), request.getSender().getId());
    }

    @Test
    public void denyRequest() {
        Request request = new Request();

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.verifyDeny(1L, user)).thenReturn(request);

        requestController.denyRequest(1L);

        verify(requestService, times(1)).verifyDeny(1L, user);
        verify(requestService, times(1)).delete(request);
    }

    @Test
    public void acceptRequest() {
        UserModel sender = new UserModel();
        sender.setId(2);
        Request request = new Request(sender, userModel);

        Chat chat = new Chat(sender, userModel);
        chat.setId(1);
        chat.setCreatedAt(dateTime);
        chat.setUpdatedAt(dateTime);

        request.setReceiver(userModel);
        request.setSender(sender);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.verifyAccept(1L, user)).thenReturn(request);
        when(chatService.create(userModel, sender)).thenReturn(chat);

        ChatDto chatDto = requestController.acceptRequest(1L);

        assertEquals(chatDto.getId(), chat.getId());
        assertEquals(chatDto.getFirstUser().getId(), sender.getId());
        assertEquals(chatDto.getSecondUser().getId(), userModel.getId());
        assertEquals(chatDto.getUpdatedAt(), dateTime.toString());
        assertEquals(chatDto.getCreatedAt(), dateTime.toString());
        verify(requestService, times(1)).verifyAccept(1L, user);
        verify(requestService, times(1)).delete(request);
    }

    @Test
    public void findAll(){
        UserModel sender = new UserModel();
        sender.setId(2);
        LocalDateTime dateTime = LocalDateTime.of(2021, 9, 9, 9, 10);

        Request request = new Request(sender, userModel);
        Request request1 = new Request(userModel, sender);
        Request request2 = new Request(sender, userModel);

        request.setCreatedAt(dateTime);
        request1.setCreatedAt(dateTime);
        request2.setCreatedAt(dateTime);

        request.setUpdatedAt(dateTime);
        request1.setUpdatedAt(dateTime);
        request2.setUpdatedAt(dateTime);

        request.setId(1);
        request1.setId(2);
        request2.setId(3);

        List<Request> requests = List.of(request, request1, request2);
        Page<Request> page = new PageImpl<>(requests);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.findAll(user.getId(), 5, "2021-09-09", 10L)).thenReturn(page);

        PageDto<RequestDto> pageDto = requestController.findAll(5, "2021-09-09", 10L);

        assertEquals(pageDto.getCount(), pageDto.getCount());
        assertEquals(pageDto.getData().get(0).getId(), request.getId());
        assertEquals(pageDto.getData().get(0).getSender().getId(), sender.getId());
        assertEquals(pageDto.getData().get(0).getReceiver().getId(), user.getId());
        assertEquals(pageDto.getData().get(0).getCreatedAt(), dateTime.toString());
        assertEquals(pageDto.getData().get(0).getId(), request.getId());
        assertEquals(pageDto.getData().get(1).getId(), request1.getId());
        assertEquals(pageDto.getData().get(2).getId(), request2.getId());
    }

    @Test
    public void findAllWithNullLastId(){
        UserModel sender = new UserModel();
        sender.setId(2);
        LocalDateTime dateTime = LocalDateTime.of(2021, 9, 9, 9, 10);

        Request request = new Request(sender, userModel);
        Request request1 = new Request(userModel, sender);
        Request request2 = new Request(sender, userModel);

        request.setCreatedAt(dateTime);
        request1.setCreatedAt(dateTime);
        request2.setCreatedAt(dateTime);

        request.setUpdatedAt(dateTime);
        request1.setUpdatedAt(dateTime);
        request2.setUpdatedAt(dateTime);

        request.setId(1);
        request1.setId(2);
        request2.setId(3);

        List<Request> requests = List.of(request, request1, request2);
        Page<Request> page = new PageImpl<>(requests);

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(requestService.findAll(user.getId(), 5, "2021-09-09", 0)).thenReturn(page);

        PageDto<RequestDto> pageDto = requestController.findAll(5, "2021-09-09", null);

        assertEquals(pageDto.getCount(), pageDto.getCount());
        assertEquals(pageDto.getData().get(0).getId(), request.getId());
        assertEquals(pageDto.getData().get(0).getSender().getId(), sender.getId());
        assertEquals(pageDto.getData().get(0).getReceiver().getId(), user.getId());
        assertEquals(pageDto.getData().get(0).getCreatedAt(), dateTime.toString());
        assertEquals(pageDto.getData().get(0).getId(), request.getId());
        assertEquals(pageDto.getData().get(1).getId(), request1.getId());
        assertEquals(pageDto.getData().get(2).getId(), request2.getId());
    }
}
