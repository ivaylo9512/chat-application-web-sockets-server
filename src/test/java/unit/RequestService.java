package unit;

import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.Request;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.RequestRepository;
import com.chat.app.services.RequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestService {
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    public void findById_withNonExistingUser_shouldThrow() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> requestService.findById(1L)
        );

        assertEquals(thrown.getMessage(), "Request not found.");
    }
    @Test
    public void findById_WithRequestThatDoesNotBelongToUser_shouldThrow() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(1);
        sender.setId(3);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(receiver, sender)));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> requestService.findById(1L, 2L)
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }


    @Test
    public void verifyAccept_WithIncorrectReceiver_shouldThrow() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        sender.setId(3);
        receiver.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(3);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> requestService.verifyAccept(1L, new UserDetails(loggedUser, authorities))
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test()
    public void verifyAccept() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(2);
        sender.setId(1);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(2);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyAccept(1L, new UserDetails(loggedUser, authorities));
    }

    @Test
    public void verifyDeny_WithIncorrectId_shouldThrow() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(1);
        sender.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(3);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities))
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }

    @Test()
    public void verifyDeny_WithSender() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(1);
        receiver.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(2);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities));
    }

    @Test()
    public void verifyDeny_WithReceiver() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(1);
        sender.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(1);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities));
    }

    @Test
    public void findRequest_WithNonexistentRequest(){
        when(requestRepository.findRequest(1, 2)).thenReturn(null);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> requestService.findRequest(1, 2)
        );

        assertEquals(thrown.getMessage(), "Request not found.");
    }

    @Test
    public void deleteById_withNonExistingId_shouldThrow() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> requestService.deleteById(1L, 2L)
        );

        assertEquals(thrown.getMessage(), "Request not found.");
    }

    @Test
    public void deleteById_WithRequestThatDoesNotBelongToUser_shouldThrow() {
        UserModel receiver = new UserModel();
        UserModel sender = new UserModel();

        receiver.setId(1);
        sender.setId(3);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(receiver, sender)));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> requestService.deleteById(1L, 2L)
        );

        assertEquals(thrown.getMessage(), "Unauthorized.");
    }
}
