package unit;

import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.Request;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.RequestRepository;
import com.chat.app.services.RequestServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestService {
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test(expected = EntityNotFoundException.class)
    public void findById_withNonExistingUser_shouldThrow() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        requestService.findById(1L);
    }

    @Test(expected = UnauthorizedException.class)
    public void verifyAccept_WithIncorrectReceiver_shouldThrow() {
        UserModel receiver = new UserModel();
        receiver.setId(1);
        UserModel sender = new UserModel();
        receiver.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(3);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyAccept(1L, new UserDetails(loggedUser, authorities));
    }

    @Test()
    public void verifyAccept() {
        UserModel receiver = new UserModel();
        receiver.setId(2);
        UserModel sender = new UserModel();
        sender.setId(1);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(2);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyAccept(1L, new UserDetails(loggedUser, authorities));
    }

    @Test(expected = UnauthorizedException.class)
    public void verifyDeny_WithIncorrectId_shouldThrow() {
        UserModel receiver = new UserModel();
        receiver.setId(1);
        UserModel sender = new UserModel();
        sender.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(3);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities));
    }

    @Test()
    public void verifyDeny_WithSender() {
        UserModel receiver = new UserModel();
        receiver.setId(1);
        UserModel sender = new UserModel();
        receiver.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(2);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities));
    }

    public void verifyDeny_WithReceiver() {
        UserModel receiver = new UserModel();
        receiver.setId(1);
        UserModel sender = new UserModel();
        receiver.setId(2);

        UserModel loggedUser = new UserModel("username", "password", "ROLE_ADMIN");
        loggedUser.setId(1);
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

        when(requestRepository.findById(1L)).thenReturn(Optional.of(new Request(sender, receiver)));

        requestService.verifyDeny(1L, new UserDetails(loggedUser, authorities));
    }
}
